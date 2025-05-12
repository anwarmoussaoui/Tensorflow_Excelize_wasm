

// Global prediction function
globalThis.predictHouse = function(houseFeatures) {
    const inputTensor = globalThis.tensor2d([houseFeatures]);
    const prediction = model.predict(inputTensor);
    prediction.print();
    return prediction;
};