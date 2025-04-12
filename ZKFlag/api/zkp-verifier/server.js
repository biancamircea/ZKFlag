const express = require("express");
const snarkjs = require("snarkjs");
const fs = require("fs");
const bodyParser = require("body-parser");

const app = express();
app.use(bodyParser.json());

const verificationKeyFile = "./verification_key.json";
const verificationKeyLocation = "./verification_key_location.json";

app.post("/verifyProof", async (req, res) => {
    try {
        const { proof, publicSignals } = req.body;

        const verificationKey = JSON.parse(fs.readFileSync(verificationKeyFile));

        const isValid = await snarkjs.plonk.verify(verificationKey, publicSignals, proof);

        res.json({
            isValid
        });
    } catch (error) {
        console.error("Error verifying proof:", error);
        res.status(500).json({ error: "Error verifying proof", details: error.message });
    }
});

app.post("/verifyLocationProof", async (req, res) => {
    try {
        const { proof, publicSignals } = req.body;

        const vkLocation = JSON.parse(fs.readFileSync(verificationKeyLocation));

        const isValid = await snarkjs.plonk.verify(vkLocation, publicSignals, proof);

        res.json({
            isValid
        });
    } catch (error) {
        console.error("Error verifying location proof:", error);
        res.status(500).json({ error: "Error verifying location proof" });
    }
});


app.listen(4000, "0.0.0.0", () => {
    console.log("Server running on port 4000");
});
