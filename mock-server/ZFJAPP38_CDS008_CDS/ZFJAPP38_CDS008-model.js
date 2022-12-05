// const uuid = require('uuid/v1');
const data = require('./ZFJAPP38_CDS008-data.js').data;

module.exports = {
    data,
    getZfjapp38Cds008: function() {
        return this.data;
    },
    findZfjapp38Cds008: function(name, type, numb) {
        return this.getZfjapp38Cds008().find(function(element) {
            return element.Name == name &&
                   element.Type == type &&
                   element.Numb == numb;
        });
    },
    filterNameZfjapp38Cds008: function(name) {
        return this.getZfjapp38Cds008().filter(function(element) {
            return element.Name == name;
        });
    }
};
