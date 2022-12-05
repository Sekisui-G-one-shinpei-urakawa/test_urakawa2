const express = require('express');
const app = express();

const bupaApi = require('./business-partner/business-partner-api.js');
const socialMediaApi = require('./social-media-accounts/social-media-accounts-api.js');
const timeSheetApi = require('./timeSheetEntryCollection/timeSheetEntryCollection-api.js');
const timeOff = require('./time-off/api.js');
const zfjapp38Cds007Api = require('./ZFJAPP38_ODSK003_SRV/ZFJAPP38_CDS007-api.js');
const zfjapp38Cds008Api = require('./ZFJAPP38_CDS008_CDS/ZFJAPP38_CDS008-api.js');
const zs4z0001srvApi = require('./Z_S4Z0001_SRV/Z_S4Z0001_SRV-api.js');

const logRequests = function(req, res, next) {
    console.log(`Request: ${req.method} ${req.originalUrl}`)
    next();
};

app.disable('x-powered-by');
app.disable('ETag');
app.disable('Date');
// app.disable('Connection');
// app.disable('Keep-Alive');
const sendFakeCsrfToken = function(req, res, next) {
    // res.removeHeader("ETag");
    // res.removeHeader('Date');
    // res.removeHeader('Connection');
    // res.removeHeader('Keep-Alive');
    res.status(202);
    res.header('Content-Type', 'multipart/mixed; boundary=9AF244D903F4401055098657640B86C30')
    res.header('dataserviceversion', '2.0')
    res.header('x-csrf-token', 'dummyToken123')
    res.header('set-cookie', ['cookie'])
    res.header('cache-control', 'no-cache, no-store, must-revalidate')
    res.header('sap-processing-info', 'ODataBEP=,crp=,RAL=,st=,MedCacheHub=,codeployed=X,softstate=')
    res.header('sap-server', 'true')
    res.header('sap-perf-fesrec', '143107.000000')
    // res.header('Content-encoding', 'gzip')
    next()
};

app.use(logRequests);
app.use(sendFakeCsrfToken);

app.use('/sap/opu/odata/sap/API_BUSINESS_PARTNER', bupaApi);
app.use('/sap/opu/odata/sap/YY1_BPSOCIALMEDIA_CDS', socialMediaApi);
app.use('/sap/opu/odata/sap/API_MANAGE_WORKFORCE_TIMESHEET', timeSheetApi);
app.use('/odata/v2', timeOff);
app.use('/sap/opu/odata/sap/ZFJAPP38_ODSK003_SRV', zfjapp38Cds007Api);
app.use('/sap/opu/odata/sap/ZFJAPP38_CDS008_CDS', zfjapp38Cds008Api);
app.use('/sap/opu/odata/sap/Z_S4Z0001_SRV', zs4z0001srvApi);
//app.use('/PcUploadSet', zs4z0001srvApi);

app.get('/', function(req, res) {
    res.set('Content-Type', 'multipart/mixed');
    res.send(`<html>
    <head>
        <title>OData Mock Service for Business Partner API of SAP S/4HANA Cloud</title>
    </head>
    <body>
        <div>OData mock service for Business Partner API of SAP S/4HANA Cloud is running at <a href="/sap/opu/odata/sap/API_BUSINESS_PARTNER">/sap/opu/odata/sap/API_BUSINESS_PARTNER</a>.</div>
        <div>OData mock service for Business Partner Social Media custom API is running at <a href="/sap/opu/odata/sap/YY1_BPSOCIALMEDIA_CDS">/sap/opu/odata/sap/YY1_BPSOCIALMEDIA_CDS</a>.</div>
        <div>OData mock service for Timesheet API is running at <a href="/sap/opu/odata/sap/API_MANAGE_WORKFORCE_TIMESHEET">/sap/opu/odata/sap/API_MANAGE_WORKFORCE_TIMESHEET</a>.</div>
        <div>OData mock service for Employee Central Time Off service of SAP SuccessFactors is running at <a href="/odata/v2/EmployeeTime">/odata/v2/EmployeeTime</a>.</div>
        <div>OData mock service for ZFJAPP38_CDS007 of SAP S/4HANA Cloud is running at <a href="/sap/opu/odata/sap/ZFJAPP38_ODSK003_SRV">/sap/opu/odata/sap/ZFJAPP38_ODSK003_SRV</a>.</div>
        <div>OData mock service for ZFJAPP38_CDS008 of SAP S/4HANA Cloud is running at <a href="/sap/opu/odata/sap/ZFJAPP38_CDS008_CDS">/sap/opu/odata/sap/ZFJAPP38_CDS008_CDS</a>.</div>
        <div>OData mock service for Z_S4Z0001_SRV of SAP S/4HANA Cloud is running at <a href="/sap/opu/odata/sap/Z_S4Z0001_SRV">/sap/opu/odata/sap/Z_S4Z0001_SRV</a>.</div>
    </body>
</html>`);
});

module.exports = app;
