const express = require('express');
const bodyParser = require('body-parser');
const router = express.Router();

const odata = require('../odata-helpers.js');
const model = require('./ZFJAPP38_CDS008-model.js');

const retrieveAllZfjapp38Cds008 = function(req, res, next) {
    console.log('Reading ZFJAPP38_CDS008 entity set');
    res.result = model.getZfjapp38Cds008();
    next();
};

const retrieveSingleZfjapp38Cds008 = function(req, res, next) {
    console.log(`Reading ZFJAPP38_CDS008 (${req.params.name},${req.params.type},${req.params.numb})`);
    res.result = model.findZfjapp38Cds008(req.params.name, req.params.type, req.params.numb);
    next();
};

const retrieveByNameZfjapp38Cds008 = function(req, res, next) {
    console.log(`Reading ZFJAPP38_CDS008 (${req.params.name})`);
    res.result = model.filterNameZfjapp38Cds008(req.params.name);
    next();
};

// Serve EDMX file for /$metadata
router.get('/([$])metadata', function(req, res) {
    const options = {
        root: __dirname + '/',
        headers: {
            'Content-Type': 'application/xml'
        }
    };
    console.log('Serving metadata for ZFJAPP38_CDS008');
    res.sendFile('ZFJAPP38_CDS008.edmx', options, function(err) {
        if(err) {
            console.error('No metadata file found at ZFJAPP38_CDS008_CDS/ZFJAPP38_CDS008.edmx. Please check the documentation on how to retrieve and where to store this file.')
            res.sendStatus(404);
        }
    });
});

router.post('/([$])batch', bodyParser.text({ type: () => true }), odata.batch, odata.set201Created);

router.route('/ZFJAPP38_CDS008')
    .get(retrieveAllZfjapp38Cds008, odata.middlewareForSet());

router.route('/ZFJAPP38_CDS008\\((Name=)?(\':name\'|%27:name%27),(Type=)?(\':type\'|%27:type%27),(Numb=)?(\':numb\'|%27:numb%27)\\)')
    .get(retrieveSingleZfjapp38Cds008, odata.middlewareForEntity());

router.route('/ZFJAPP38_CDS008\\((Name=)?(\':name\'|%27:name%27)\\)')
    .get(retrieveByNameZfjapp38Cds008, odata.middlewareForEntity());

router.get('/', function(req, res) {
    res.json({
        "d": {
            "EntitySets": [
                "ZFJAPP38_CDS008"
            ]
        }
    });
});

module.exports = router;
