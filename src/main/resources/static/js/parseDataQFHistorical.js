var isdsi = false;
var isqf = true;
var isdqf = false;

const DEFAULT_CATEGORY = "Default"

var url;
if (getParameterByName('id').length !== 0) {
    url = parseURLSimple("../api/strategicIndicators/qualityFactors/historical");
} else {
    var profileId = sessionStorage.getItem("profile_id");
    url = parseURLSimple("../api/qualityFactors/historical?profile="+profileId);
}

//initialize data vectors
var texts = [];
var value = [];
var labels = [];
var ids = [];
var printMetrics = false;
var categories = [];

let orderedFactorsDB = [];

function getData() {
    texts = [];
    value = [];
    labels = [];
    ids = [];
    //get data from API
    jQuery.ajax({
        dataType: "json",
        url: url,
        data: {
            "from": $('#datepickerFrom').val(),
            "to": $('#datepickerTo').val()
        },
        cache: false,
        type: "GET",
        async: true,
        success: function (data) {
            if (getParameterByName('id').length !== 0) {
                data = data[0].factors;
            }
            sortDataAlphabetically(data);
            console.log("inside " + url);
            console.log(data);
            j = 0;
            var line = [];
            if (data[j]) {
                last = data[j].id;
                texts.push(data[j].name);
                labels.push([data[j].name]);
                ids.push(data[j].id);
            }
            while (data[j]) {
                //check if we are still on the same Factor
                if (data[j].id != last) {
                    value.push([line]);
                    line = [];
                    last = data[j].id;
                    texts.push(data[j].name);
                    labels.push([data[j].name]);
                    ids.push(data[j].id);
                }
                //push date and value to line vector
                if (!isNaN(data[j].value.first)) {
                    line.push({
                        x: data[j].date,
                        y: data[j].value.first
                    });
                }
                ++j;
            }
            //push line vector to values vector for the last metric
            if (data[j - 1]) {
                value.push([line]);
            }
            getFactorsCategories();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 409)
                warningUtils("Error","Your datasource and DB categories IDs do not match.");
            else if (jqXHR.status == 400)
                warningUtils("Error", "Datasource connection failed.");
        }
    });
}

function sortDataAlphabetically (data) {
    function compare (a, b) {
        if (a.name < b.name) return -1;
        else if (a.name > b.name) return 1;
        else return 0;
    }
    data.sort(compare);
}

function getFactorsCategories () {
    jQuery.ajax({
        url: "../api/factors/categories",
        type: "GET",
        async: true,
        success: function (response) {
            categories = response;
            getFactorList();
        }
    });
}

function getFactorList() {
    jQuery.ajax({
        url: "../api/qualityFactors",
        type: "GET",
        async: true,
        success: function (dataF) {
            sortFactorDB(dataF);
            drawChart();
        }
    });
}

function sortFactorDB (data) {
    ids.forEach( function (id) {
        orderedFactorsDB.push(
            data.find(elem => elem.externalId === id)
        )
    })
}

window.onload = function() {
    getData();
};
