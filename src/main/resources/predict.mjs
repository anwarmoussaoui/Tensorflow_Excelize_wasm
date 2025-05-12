
globalThis.predictHouse = function(houseFeatures) {
    //const inputTensor = globalThis.tf.tensor2d([houseFeatures]);
    const prediction = globalThis.model.predict(houseFeatures);
    prediction.print();
    return prediction;
};