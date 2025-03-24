const express = require("express");
const snarkjs = require("snarkjs");
const fs = require("fs");
const bodyParser = require("body-parser");

const app = express();
app.use(bodyParser.json());

const verificationKeyFile = "./verification_key.json";

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


app.listen(4000, "0.0.0.0", () => {
    console.log("Server running on port 4000");
});
