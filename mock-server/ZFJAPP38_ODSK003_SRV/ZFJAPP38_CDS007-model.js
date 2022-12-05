// const uuid = require('uuid/v1');
const data = require('./ZFJAPP38_CDS007-data.js').data;

const today = function () {
    const time = Date.now();
    return time - (time % 24 * 60 * 60 * 1000);
}

module.exports = {
    data,
    newZfjapp38Cds007: function (companycode, fiscalyear, accountingdocument, accountingdocumentitem) {
        return Object.seal({
            "__metadata": {
                "id": `https://{host}:{port}/sap/opu/odata/sap/ZFJAPP38_ODSK003_SRV/ZFJAPP38_CDS007(Companycode='${companycode}',Fiscalyear='${fiscalyear}',Accountingdocument='${accountingdocument}',Accountingdocumentitem='${accountingdocumentitem}')`,
                "uri": `https://{host}:{port}/sap/opu/odata/sap/ZFJAPP38_ODSK003_SRV/ZFJAPP38_CDS007(Companycode='${companycode}',Fiscalyear='${fiscalyear}',Accountingdocument='${accountingdocument}',Accountingdocumentitem='${accountingdocumentitem}')`,
                "type": "ZFJAPP38_ODSK003_SRV.ZFJAPP38_CDS007Type"
            },
            "Companycode": companycode,
            "Fiscalyear": fiscalyear,
            "Accountingdocument": accountingdocument,
            "Accountingdocumentitem": accountingdocumentitem,
            "Documentdate": "",
            "Postingdate": "",
            "Accountingdocumentcreationdate": "",
            "Creationtime": "",
            "Accountingdocumenttype": "",
            "Postingkey": "",
            "Glaccount": "",
            "Supplier": "",
            "Financialaccounttype": "",
            "Transactioncurrency": "",
            "Companycodecurrency": "",
            "Amountintransactioncurrency": "",
            "Amountincompanycodecurrency": "",
            "Documentitemtext": "",
            "Paymentmethod": "",
            "Duecalculationbasedate": "",
            "Duecalculationbasedate_Open": ""
        });
    },
    getZfjapp38Cds007: function() {
        return this.data;
    },
    findZfjapp38Cds007: function(companycode, fiscalyear, accountingdocument, accountingdocumentitem) {
        return this.getZfjapp38Cds007().find(function(element) {
            return element.Companycode == companycode &&
                   element.Fiscalyear == fiscalyear &&
                   element.Accountingdocument == accountingdocument &&
                   element.Accountingdocumentitem == accountingdocumentitem;
        });
    },
    createAndAddZfjapp38Cds007: function(input) {
        const newZfjapp38Cds007 = this.newZfjapp38Cds007(input.Companycode, input.Fiscalyear, input.Accountingdocument, input.Accountingdocumentitem);
        Object.assign(newZfjapp38Cds007, input);
        this.getZfjapp38Cds007().push(newZfjapp38Cds007);
        return newZfjapp38Cds007;
    },
    deleteZfjapp38Cds007: function(companycode, fiscalyear, accountingdocument, accountingdocumentitem) {
        if(!this.findZfjapp38Cds007(companycode, fiscalyear, accountingdocument, accountingdocumentitem)) {
            throw new Error(`Cannot delete ZFJAPP38_CDS007: zfjapp38Cds007 with key (${companycode},${fiscalyear},${accountingdocument},${accountingdocumentitem}) does not exist.`);
        }
        this.data = this.getZfjapp38Cds007()
            .filter( (zfjapp38Cds007) => zfjapp38Cds007.Companycode != companycode
                    && zfjapp38Cds007.Fiscalyear != fiscalyear
                    && zfjapp38Cds007.Accountingdocument != accountingdocument
                    && zfjapp38Cds007.Accountingdocumentitem != accountingdocumentitem);
    },
    modifyZfjapp38Cds007: function(companycode, fiscalyear, accountingdocument, accountingdocumentitem, input) {
        const zfjapp38Cds007ToUpdate = this.findZfjapp38Cds007(companycode, fiscalyear, accountingdocument, accountingdocumentitem);
        if(!zfjapp38Cds007ToUpdate) {
            throw new Error(`Cannot modify ZFJAPP38_CDS007: zfjapp38Cds007 with key (${companycode},${fiscalyear},${accountingdocument},${accountingdocumentitem}) does not exist.`);
        }
        if((input.Companycode && input.Companycode != companycode) ||
                 (input.Fiscalyear && input.Fiscalyear != fiscalyear) ||
                 (input.Accountingdocument && input.Accountingdocument != accountingdocument) ||
                 (input.Accountingdocumentitem && input.Accountingdocumentitem != accountingdocumentitem)
                 ) {
            throw new Error(`Cannot modify ZFJAPP38_CDS007: key fields must not be changed`);
        }
        Object.assign(zfjapp38Cds007ToUpdate, input);
    }
};
