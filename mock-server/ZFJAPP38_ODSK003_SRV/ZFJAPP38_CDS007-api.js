const express = require('express');
const bodyParser = require('body-parser');
const router = express.Router();

const odata = require('../odata-helpers.js');
const model = require('./ZFJAPP38_CDS007-model.js');

const retrieveAllZfjapp38Cds007 = function(req, res, next) {
    console.log('Reading ZFJAPP38_CDS007 entity set');
    res.result = model.getZfjapp38Cds007();
    next();
};

const retrieveSingleZfjapp38Cds007 = function(req, res, next) {
    console.log(`Reading ZFJAPP38_CDS007 (${req.params.companycode},${req.params.fiscalyear},${req.params.accountingdocument},${req.params.accountingdocumentitem})`);
    res.result = model.findZfjapp38Cds007(req.params.companycode, req.params.fiscalyear, req.params.accountingdocument, req.params.accountingdocumentitem);
    next();
};

const createZfjapp38Cds007 = function(req, res, next) {
    console.log('Creating ZFJAPP38_CDS007');
    res.result = model.createAndAddZfjapp38Cds007(req.body);
    console.log(`Created ZFJAPP38_CDS007 (${res.result.Companycode},${res.result.Fiscalyear},${res.result.Accountingdocument},${res.result.Accountingdocumentitem})`)
    next();
};

const deleteZfjapp38Cds007 = function(req, res, next) {
    console.log(`Deleting ZFJAPP38_CDS007 (${req.params.companycode},${req.params.fiscalyear},${req.params.accountingdocument},${req.params.accountingdocumentitem})`);
    model.deleteZfjapp38Cds007(req.params.companycode, req.params.fiscalyear, req.params.accountingdocument, req.params.accountingdocumentitem);
    next();
};

const modifyZfjapp38Cds007 = function(req, res, next) {
    console.log(`Modifying ZFJAPP38_CDS007 (${req.params.companycode},${req.params.fiscalyear},${req.params.accountingdocument},${req.params.accountingdocumentitem})`);
    model.modifyZfjapp38Cds007(req.params.companycode, req.params.fiscalyear, req.params.accountingdocument, req.params.accountingdocumentitem, req.body);
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
    console.log('Serving metadata for ZFJAPP38_CDS007');
    res.sendFile('ZFJAPP38_CDS007.edmx', options, function(err) {
        if(err) {
            console.error('No metadata file found at ZFJAPP38_ODSK003_SRV/ZFJAPP38_CDS007.edmx. Please check the documentation on how to retrieve and where to store this file.')
            res.sendStatus(404);
        }
    });
});

router.post('/([$])batch', bodyParser.text({ type: () => true }), odata.batch, odata.set201Created);

const handlersForZfjapp38Cds007Update = odata.middlewareForUpdate(retrieveSingleZfjapp38Cds007, modifyZfjapp38Cds007);

router.route('/ZFJAPP38_CDS007')
    .get(retrieveAllZfjapp38Cds007, odata.middlewareForSet())
    .post(odata.middlewareForCreate(createZfjapp38Cds007));

router.route('/ZFJAPP38_CDS007\\((Companycode=)?(\':companycode\'|%27:companycode%27),(Fiscalyear=)?(\':fiscalyear\'|%27:fiscalyear%27),(Accountingdocument=)?(\':accountingdocument\'|%27:accountingdocument%27),(Accountingdocumentitem=)?(\':accountingdocumentitem\'|%27:accountingdocumentitem%27)\\)')
    .get(retrieveSingleZfjapp38Cds007, odata.middlewareForEntity())
    .delete(retrieveSingleZfjapp38Cds007, odata.send404IfNotFound, deleteZfjapp38Cds007, odata.send204NoContent)
    .patch(handlersForZfjapp38Cds007Update)
    .put(handlersForZfjapp38Cds007Update);

router.get('/', function(req, res) {
    res.json({
        "d": {
            "EntitySets": [
                "ZFJAPP38_CDS007"
            ]
        }
    });
});

module.exports = router;
