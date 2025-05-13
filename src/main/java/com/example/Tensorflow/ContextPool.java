package com.example.Tensorflow;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
@Component
public class ContextPool {

    private static Map<String, String> getLanguageOptions() {
        Map<String, String> options = new HashMap<>();
        options.put("js.ecmascript-version", "2023");
        options.put("js.top-level-await", "true");
        options.put("js.webassembly", "true");
        options.put("js.performance", "true");
        options.put("js.commonjs-require", "true");
        options.put("js.esm-eval-returns-exports", "true");
        options.put("js.unhandled-rejections", "throw");
        options.put("js.commonjs-require-cwd", Paths.get("./").toAbsolutePath().toString());
        return options;
    }

        private final Context context;
        public ContextPool () throws IOException {

        Context context = Context.newBuilder("js", "wasm")
                .allowHostAccess(HostAccess.ALL)
                .allowAllAccess(true)
                .options(getLanguageOptions())
                .build();


            ExcelizePool excelizePool = new ExcelizePool();
            Context context1 = excelizePool.getContext();
            byte[] excelBytes = Files.readAllBytes(Paths.get("./src/main/resources/data.xlsx"));


            Value readFunc = context1.getBindings("js").getMember("readExcel");
            Value bufferArray = readFunc.execute(excelBytes);
            context.eval("js", "globalThis.self = globalThis;");
            context.eval("js", "globalThis.window = globalThis;");
            context.eval("js", "globalThis.document = { body: {} };");
            context.eval("js", "globalThis.window.location = { href: '' };");


            byte[] tsfjswasm = Files.readAllBytes(Paths.get("./src/main/resources/tfjs-backend-wasm-simd.wasm"));
            context.getBindings("js").putMember("tsfwasm", tsfjswasm);

            String polyfill= """
                               (() => {
                              const NativeURL = globalThis.URL;
                    
                              class FakeURL {
                                constructor(input, base) {
                                  this.href = input;
                                }
                    
                                toString() {
                                  return this.href;
                                }
                              }
                    
                              globalThis.URL = FakeURL;
                    
                              globalThis.fetch = async function (url) {
                                const tsfwasm = './tfjs-backend-wasm-simd.wasm'
                                const target = (typeof url === 'object' && 'href' in url) ? url.href : url;
                                if (target === tsfwasm) {
                                  return {
                                    async arrayBuffer() {
                                      return globalThis.tsfwasm;
                                    },
                                    ok: true,
                                    status: 200,
                                  };
                                }
                                else {
                                  throw new Error(`Unhandled fetch to: ${target}`);
                                }
                              };
                            })();
                    if (typeof WebAssembly.instantiateStreaming !== "function") {
                      WebAssembly.instantiateStreaming = async (sourcePromise, importObject) => {
                        // Assume `globalThis.tsfwasm` is already a Uint8Array or ArrayBuffer
                        const buffer = globalThis.tsfwasm instanceof Uint8Array
                          ? globalThis.tsfwasm.buffer
                          : globalThis.tsfwasm;
                    
                        return WebAssembly.instantiate(new Uint8Array(buffer), importObject);
                      };
                    }
                    """
                    ;
            context.eval("js",polyfill);
            Source bundleSrc = Source.newBuilder("js",ContextPool.class.getResource("/bundle/bundle.mjs")).build();
            context.eval(bundleSrc);
            context.getBindings("js").getMember("trainModel").execute(bufferArray);
            System.out.println( context.getBindings("js").getMember("savedArtifacts"));
            Context modelContext = Context.newBuilder("js", "wasm")
                    .allowHostAccess(HostAccess.ALL)
                    .allowAllAccess(true)
                    .options(getLanguageOptions())
                    .build();

            modelContext.eval("js", "globalThis.self = globalThis;");
            modelContext.eval("js", "globalThis.window = globalThis;");
            modelContext.eval("js", "globalThis.document = { body: {} };");
            modelContext.eval("js", "globalThis.window.location = { href: '' };");
            modelContext.getBindings("js").putMember("tsfwasm", tsfjswasm);
            modelContext.eval("js",polyfill);

            modelContext.eval(bundleSrc);
            Value mdl = context.getBindings("js").getMember("savedArtifacts");
            modelContext.getBindings("js").putMember("savedArtifacts",mdl);
            this.context=modelContext;
    }

    public Context getContext() {
        return context;
    }
}
