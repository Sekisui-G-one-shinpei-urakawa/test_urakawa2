const express = require('express');
const bodyParser = require('body-parser');
const router = express.Router();

const odata = require('../odata-helpers.js');
const model = require('./Z_S4Z0001_SRV-model.js');

const retrieveAllPcUpload = function(req, res, next) {
    console.log('Reading PcUploadSet entity set');
    res.result = model.getPcUpload();
    next();
};

const retrieveSinglePcUpload = function(req, res, next) {
    console.log(`Reading PcUploadSet (${req.params.seqno})`);
    res.result = model.findPcUpload(req.params.seqno);
    next();
};

const createPcUpload = function(req, res, next) {
    console.log('Creating PcUpload');
    res.result = model.createPcUpload(req.body);
    console.log(`Created PcUpload (${res.result.seqno})`);
    next();
};

const modifyPcUpload = function(req, res, next) {
    console.log(`Modifying PcUploadSet (${req.params.seqno})`);
    res.result = model.modifyPcUpload(req.params.seqno, req.body);
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
    console.log('Serving metadata for Z_S4Z0001_SRV');
    res.sendFile('Z_S4Z0001_SRV.edmx', options, function(err) {
        if(err) {
            console.error('No metadata file found at Z_S4Z0001_SRV_CDS/Z_S4Z0001_SRV.edmx. Please check the documentation on how to retrieve and where to store this file.')
            res.sendStatus(404);
        }
    });
});

router.post('/([$])batch', bodyParser.text({ type: () => true }), odata.batch, odata.set201Created);

const handlersForPcUploadUpdate = odata.middlewareForUpdate(retrieveSinglePcUpload, modifyPcUpload);

router.route('/PcUploadSet')
    .get(retrieveAllPcUpload, odata.middlewareForSet())
    .post(odata.middlewareForCreate(createPcUpload));

router.route('/PcUploadSet\\((Seqno=)?(\':seqno\'|%27:seqno%27)\\)')
    .get(retrieveSinglePcUpload, odata.middlewareForEntity())
    .patch(retrieveSinglePcUpload, odata.middlewareForEntity())
    .put(handlersForPcUploadUpdate);

router.get('/', function(req, res) {
    res.json({
        "d": {
            "EntitySets": [
                "PcUploadSet"
            ]
        }
    });
});

module.exports = router;
