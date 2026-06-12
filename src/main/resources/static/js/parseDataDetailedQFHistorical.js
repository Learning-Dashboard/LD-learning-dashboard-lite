var isdsi = false;
var isqf = false;
var isdqf = true;

const DEFAULT_CATEGORY = "Default"

var profileId = sessionStorage.getItem("profile_id");
var url = parseURLComposed("../api/qualityFactors/metrics/historical?profile="+profileId);

//initialize data vectors
var texts = [];
var ids = [];
var labels = [];
var value = [];
let metricsDB = [];
var categories = [];
var printMetrics = false;

function getData() {
    texts = [];
    ids = [];
    labels = [];
    value = [];
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
            console.log(url);
            console.log("getData() in QF Historical");
            sortDataAlphabetically(data);
            console.log(data);
            for (i = 0; i < data.length; ++i) {
                //for each qf save name to texts vector and id to ids vector
                if (data[i].metrics.length > 0) {
                    texts.push(data[i].name);
                    ids.push(data[i].id);

                    value.push([[]]);
                    last = data[i].metrics[0].id;
                    labels.push([data[i].metrics[0].name]);
                    k = 0;
                    for (j = 0; j < data[i].metrics.length; ++j) {
                        //check if we are still on the same metric
                        if (last !== data[i].metrics[j].id) {
                            // New metric
                            labels[i].push(data[i].metrics[j].name);
                            last = data[i].metrics[j].id;
                            ++k;
                            value[i].push([]);
                        }
                        //push date and value to values vector
                        if (!isNaN(data[i].metrics[j].value)) {
                            value[i][k].push(
                                {
                                    x: data[i].metrics[j].date,
                                    y: data[i].metrics[j].value
                                }
                            );
                        }
                    }
                } else {
                    data.splice(i, 1);
                    --i;
                }
            }
            console.log(texts);
            console.log(ids);
            console.log(labels);
            console.log(value);
            getMetricsCategories();
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

function getMetricsCategories () {
    jQuery.ajax({
        url: "../api/metrics/categories",
        type: "GET",
        async: true,
        success: function (response) {
            categories = response;
            getMetricsWithCategory();
        }
    });
}

function getMetricsWithCategory(){
    $.ajax({
        dataType: "json",
        url: "../api/metrics",
        cache: false,
        type: "GET",
        async: true,
        success: function (dataDB) {
            metricsDB = dataDB;
            drawChart();
        }
    });
}

window.onload = function() {
    getData();
};
