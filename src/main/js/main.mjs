import 'fast-text-encoding';
import * as tf from '@tensorflow/tfjs';
import { setWasmPaths } from '@tensorflow/tfjs-backend-wasm';

// Set backend to WebAssembly
setWasmPaths('./');
await tf.setBackend('wasm');
await tf.ready();

console.log("Starting training...");




// Global prediction function
globalThis.predictHouse = function(houseFeatures) {
    const inputTensor = tf.tensor2d([houseFeatures]);
    const prediction = model.predict(inputTensor);
    prediction.print();
    return prediction;
};

let model;  //

globalThis.trainModel = async function(dataset) {
    console.log("Dataset length:", dataset.length);
    console.log(dataset);

    const inputs = [];
    const prices = [];

    // Ignore header row
    const dataRows = dataset.slice(1);

    dataRows.forEach(row => {
        const numbers = row.map(val => {
            const num = Number(val);
            return isNaN(num) ? 0 : num;
        });

        const [price, ...features] = numbers;
        prices.push([price]);
        inputs.push(features);
    });

    console.log("Sample input:", inputs[0]);
    console.log("Sample price:", prices[0]);
    console.log("All inputs numeric?", inputs.every(row => row.every(cell => typeof cell === 'number')));

    const featureTensor = tf.tensor2d(inputs);
    const min = featureTensor.min(0);
    const max = featureTensor.max(0);
    const range = max.sub(min);

    // Avoid division by zero by replacing 0s in range with 1
    const safeRange = range.add(tf.tensor1d(Array(range.shape[0]).fill(1e-7)));
    const normalizedFeatures = featureTensor.sub(min).div(safeRange);

    const labelTensor = tf.tensor2d(prices);

    model = tf.sequential();
    model.add(tf.layers.dense({ inputShape: [normalizedFeatures.shape[1]], units: 12, activation: 'relu' }));
    model.add(tf.layers.dense({ units: 6, activation: 'relu' }));
    model.add(tf.layers.dense({ units: 1 }));

    model.compile({ optimizer: 'adam', loss: 'meanSquaredError' });

    console.log("Training...");
    await model.fit(normalizedFeatures, labelTensor, {
        epochs: 200,
        batchSize: 1,
        verbose: 1,
    });

    console.log("Training complete.");
    Polyglot.export("model", model);
    Polyglot.export("tf", tf);
    console.log(tf);
}
