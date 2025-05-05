import 'fast-text-encoding';
import * as tf from '@tensorflow/tfjs';
import { setWasmPaths } from '@tensorflow/tfjs-backend-wasm';

setWasmPaths('./');
await tf.setBackend('wasm');
await tf.ready();

console.log("Training house price prediction model...");

// Assuming daa is your dataset
console.log(daa.length);
console.log(daa);

// Prepare the data
const inputs = [];
const prices = [];

// Slice the dataset to ignore headers if necessary
const dataRows = daa.slice(1);

// Check for constant columns and handle NaN or undefined values
dataRows.forEach(row => {
  const numbers = row.map(val => {
    const num = Number(val);
    return isNaN(num) ? 0 : num;  // Replace NaN with 0 if conversion fails
  });

  const [price, ...features] = numbers;

  // Push valid price and features
  prices.push([price]);
  inputs.push(features);
});

console.log("Sample input:", inputs[0]);
console.log("Sample price :" , prices[0]);
console.log("All inputs numeric?", inputs.every(row => row.every(cell => typeof cell === 'number')));


const featureTensor = tf.tensor2d(inputs);
const min = featureTensor.min(0);
const max = featureTensor.max(0);


const range = max.sub(min);
const finalFeatures = featureTensor // Avoid division by zero by setting zeros to 1

const labelTensor = tf.tensor2d(prices);

const model = tf.sequential();
model.add(tf.layers.dense({ inputShape: [finalFeatures.shape[1]], units: 12, activation: 'relu' }));
model.add(tf.layers.dense({ units: 6, activation: 'relu' }));
model.add(tf.layers.dense({ units: 1 }));

model.compile({ optimizer: 'adam', loss: 'meanSquaredError' });

console.log("Before normalization:");
featureTensor.print();

console.log("Range of features:");
range.print();

console.log("Final normalized features:");
finalFeatures.print();

await model.fit(finalFeatures, labelTensor, {
  epochs: 200,
  batchSize: 1,
  verbose: 1,
});

// Predict a new example
const newHouse = [3, 1.5, 1340, 7912, 1.5, 0, 0, 3, 1340, 0, 1955, 2005]; // Example house features

predictHouse(newHouse).array().then(arr => {
  console.log(`Predicted price: $${Math.round(arr[0][0])}`);
});

function predictHouse(house1){
    const finalNewInput = tf.tensor2d([house1]);
    const prediction = model.predict(finalNewInput);
    prediction.print();
    return prediction
}

globalThis.predictHouse = function(house1){
    const finalNewInput = tf.tensor2d([house1]);
    const prediction = model.predict(finalNewInput);
    prediction.print();
    return prediction
}

