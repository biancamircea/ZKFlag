const snarkjs = require("snarkjs");
const fs = require("fs");
const path = require("path");

async function main() {
    const args = process.argv.slice(2);
    if (args.length !== 3) {
        console.error("Usage: node verify_proof.js <verification_key> <proof> <public_signals>");
        process.exit(1);
    }

    try {
        const [vKeyPath, proofPath, publicSignalsPath] = args;

        const vKey = JSON.parse(fs.readFileSync(vKeyPath, "utf8"));
        const proof = JSON.parse(fs.readFileSync(proofPath, "utf8"));
        const publicSignals = JSON.parse(fs.readFileSync(publicSignalsPath, "utf8"));

        const isValid = await snarkjs.groth16.verify(vKey, publicSignals, proof);
        console.log(isValid ? "valid" : "invalid");
        process.exit(isValid ? 0 : 1);

    } catch (error) {
        console.error("Error:", error.message);
        process.exit(1);
    }
}

main();