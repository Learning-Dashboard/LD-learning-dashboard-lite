var isSi = true;
var isdsi = false;
var isqf = false;
var isdqf = false;


const DEFAULT_CATEGORY = "Default"

//initialize data vectors
var texts = [];
var value = [];
var labels = [];
var ids = [];
var printMetrics = false;
var categories = [];

function getData() {
    texts = [];
    value = [];
    labels = [];
    ids = [];

    console.log("sessionStorage: profile_id");
    console.log(sessionStorage.getItem("profile_id"));
    var profileId = sessionStorage.getItem("profile_id");

    //get data from API
    jQuery.ajax({
        dataType: "json",
        url: "../api/strategicIndicators/historical?profile="+profileId,
        data: {
            "from": $('#datepickerFrom').val(),
            "to": $('#datepickerTo').val()
        },
        cache: false,
        type: "GET",
        async: true,
        success: function (data) {
            console.log("inside ../api/strategicIndicators/historical")
            sortDataAlphabetically(data);
            console.log(data);
            j = 0;
            var line = [];
            if (data[j]) {
                last = data[j].id;
                texts.push(data[j].name);
                labels.push([data[j].name]);
                ids.push(data[j].id);

                data[j].probabilities.forEach(function (category) {
                    categories.push({
                        name: category.label,
                        color: category.color,
                        upperThreshold: category.upperThreshold
                    });
                });
            }
            while (data[j]) {
                //check if we are still on the same Strategic Indicator
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

            drawChart();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 409)
                warningUtils("Error","Your datasource and DB categories IDs do not match.");
            else if (jqXHR.status == 400) {
                warningUtils("Error", "Datasource connection failed.");
            }
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

window.onload = function() {
    getData();
};
