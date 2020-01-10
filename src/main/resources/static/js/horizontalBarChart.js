/*
When included on a page along with chart.js, draws a horizontal bar chart in the element

    <canvas id="chart" width="90%" ></canvas>

With data from the current URL + /data.json
 */

function getChartData(chart, url) {
    $.ajax({
        url: url,
        method: 'GET',
        dataType: 'json',
        success: function (d) {
            updateChart(chart, d);
        }
    });
}

function updateChart(chart, data) {
    chart.data = data;
    chart.update()
}

function buildDataUrl() {
    let url = window.location.pathname;
    if (url.endsWith("/")) {
        return url + 'data.json';
    } else {
        return url + '/data.json';
    }
}


$(document).ready(function(){
    const ctx = document.getElementById('chart').getContext('2d');
    const dataUrl = buildDataUrl();
    const myChart = new Chart(ctx, {
            type: 'horizontalBar',
            data: [],
            options: {
                tooltips: {
                    mode: 'index',
                    intersect: false
                },
                responsive: true,
                scales: {
                    xAxes: [{
                        stacked: true,
                    }],
                    yAxes: [{
                        stacked: true
                    }]
                }
            }
        }
    );

    getChartData(myChart, dataUrl);

    /*
    // To enable auto refresh:
    setInterval(() => {
        getChartData(myChart,'/reports/checkinbydepartment/data.json');
    }, 15000);
    */
});

