// const uuid = require('uuid/v1');
const data = require('./Z_S4Z0001_SRV-data.js').data;

module.exports = {
    data,
    newPcUpload: function (seqno) {
        return Object.seal({
            "__metadata": {
                "id": `https://{host}:{port}/sap/opu/odata/sap/Z_S4Z0001_SRV/PcUploadSet('00001')`,
                "uri": `https://{host}:{port}/sap/opu/odata/sap/Z_S4Z0001_SRV/PcUploadSet('00001')`,
                "type": "Z_S4Z0001_SRV.PcUpload"
            },
            "Seqno": "00001",
            "Comm001": "INPUT1",
            "Comm002": "INPUT2",
            "Comm003": "INPUT3",
            "Comm004": "INPUT4",
            "Comm005": "INPUT5",
            "Comm006": "INPUT6",
            "Comm007": "INPUT7",
            "Comm008": "INPUT8",
            "Comm009": "INPUT9",
            "Comm010": "INPUT10",
            "Comm011": "INPUT11",
            "Comm012": "INPUT12",
            "Comm013": "INPUT13",
            "Comm014": "INPUT14",
            "Comm015": "INPUT15",
            "Comm016": "INPUT16",
            "Comm017": "INPUT17",
            "Comm018": "INPUT18",
            "Comm019": "INPUT19",
            "Comm020": "INPUT20",
            "Comm021": "INPUT21",
            "Comm022": "INPUT22",
            "Comm023": "INPUT23",
            "Comm024": "INPUT24",
            "Comm025": "INPUT25",
            "Comm026": "INPUT26",
            "Comm027": "INPUT27",
            "Comm028": "INPUT28",
            "Comm029": "INPUT29",
            "Comm030": "INPUT30",
            "Comm031": "INPUT31",
            "Comm032": "INPUT32",
            "Comm033": "INPUT33",
            "Comm034": "INPUT34",
            "Comm035": "INPUT35",
            "Comm036": "INPUT36",
            "Comm037": "INPUT37",
            "Comm038": "INPUT38",
            "Comm039": "INPUT39",
            "Comm040": "INPUT40",
            "Comm041": "INPUT41",
            "Comm042": "INPUT42",
            "Comm043": "INPUT43",
            "Comm044": "INPUT44",
            "Comm045": "INPUT45",
            "Comm046": "INPUT46",
            "Comm047": "INPUT47",
            "Comm048": "INPUT48",
            "Comm049": "INPUT49",
            "Comm050": "INPUT50",
            "Comm051": "INPUT51",
            "Comm052": "INPUT52",
            "Comm053": "INPUT53",
            "Comm054": "INPUT54",
            "Comm055": "INPUT55",
            "Comm056": "INPUT56",
            "Comm057": "INPUT57",
            "Comm058": "INPUT58",
            "Comm059": "INPUT59",
            "Comm060": "INPUT60",
            "Comm061": "INPUT61",
            "Comm062": "INPUT62",
            "Comm063": "INPUT63",
            "Comm064": "INPUT64",
            "Comm065": "INPUT65",
            "Comm066": "INPUT66",
            "Comm067": "INPUT67",
            "Comm068": "INPUT68",
            "Comm069": "INPUT69",
            "Comm070": "INPUT70",
            "Comm071": "INPUT71",
            "Comm072": "INPUT72",
            "Comm073": "INPUT73",
            "Comm074": "INPUT74",
            "Comm075": "INPUT75",
            "Comm076": "INPUT76",
            "Comm077": "INPUT77",
            "Comm078": "INPUT78",
            "Comm079": "INPUT79",
            "Comm080": "INPUT80",
            "Comm081": "INPUT81",
            "Comm082": "INPUT82",
            "Comm083": "INPUT83",
            "Comm084": "INPUT84",
            "Comm085": "INPUT85",
            "Comm086": "INPUT86",
            "Comm087": "INPUT87",
            "Comm088": "INPUT88",
            "Comm089": "INPUT89",
            "Comm090": "INPUT90",
            "Comm091": "INPUT91",
            "Comm092": "INPUT92",
            "Comm093": "INPUT93",
            "Comm094": "INPUT94",
            "Comm095": "INPUT95",
            "Comm096": "INPUT96",
            "Comm097": "INPUT97",
            "Comm098": "INPUT98",
            "Comm099": "INPUT99",
            "Comm100": "INPUT100",
            "Comm101": "INPUT101",
            "Comm102": "INPUT102",
            "Comm103": "INPUT103",
            "MessageType": "I",
            "MessageId": "FI",
            "MessageNo": "001",
            "MessageText": "TEST1",
            "Out001": "OUTPUT1",
            "Out002": "OUTPUT2",
            "Out003": "OUTPUT3",
            "Out004": "OUTPUT4",
            "Out005": "OUTPUT5"
        });
    },
    getPcUpload: function() {
        return this.data;
    },
    findPcUpload: function(seqno) {
        return this.getPcUpload().find(function(element) {
            return element.Seqno == seqno;
        });
    },
    createPcUpload: function(input) {
        const newPcUpload = this.newPcUpload(input.seqno);
        Object.assign(newPcUpload, input);
        this.getPcUpload().push(newPcUpload);
        return newPcUpload;
    },
    modifyPcUpload: function(seqno, input) {
        const pcUploadToUpdate = this.findPcUpload(seqno);
        if(!pcUploadToUpdate) {
            throw new Error(`Cannot modify Z_S4Z0001_SRV: pcUpload with key (${seqno}) does not exist.`);
        }
        if(input.seqno && input.seqno != seqno) {
            throw new Error(`Cannot modify Z_S4Z0001_SRV: key fields must not be changed`);
        }
        Object.assign(pcUploadToUpdate, input);
        console.log('-------------------------------pcUploadToUpdate-------------------------------');
        console.log(`${pcUploadToUpdate.toString()}`);
        return pcUploadToUpdate;
    }
};
