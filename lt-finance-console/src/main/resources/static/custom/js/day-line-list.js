//加载列表数据
$(document).ready(function () {
    initTable();
});

function initTable() {
    $('#day-line-table').bootstrapTable({
        url: '/day-line/line-list',
        method: 'post',
        dataType : 'json',
        uniqueId: 'id',
        idField: 'id',
        toolbar: '#toolbar',
        showColumns: true,
        showRefresh: true,
        showToggle: true,
        pagination: true,
        queryParams: function(params) {
            var temp = collectForm();
            temp["limit"] = params.limit;
            temp["offset"] = params.offset;
            return temp;
        },
        sidePagination: "server",
        pageNumber: 1,
        pageSize: 10,
        pageList: [10, 25, 50, 100],
        columns: [{
            field: 'chk',
            checkbox: true
        }, {
            field: 'id',
            title: 'id',
            visible: false
        }, {
            field: 'tsCode',
            title: '股票代码'
        }, {
            field: 'tradeDate',
            title: '交易日期'
        }, {
            field: 'pctChg',
            title: '股票振幅',
        }, {
            field: 'operate',
            title: '操作',
            formatter: btnGroup,
            events: {
                'click .edit-btn': function (event, value, row) {
                    editEvent(row);
                },
                'click .del-btn': function (event, value, row) {
                    delEvent(row);
                },
                'click .modal-btn': function (event, value, row) {
                    modalEvent(row);
                }
            }
        }],
        ajaxOptions:{
            headers: {"Access_Token":sessionStorage.getItem("token")}
        },
        responseHandler: function (res) {
            return {
                "rows": res.data.rows,
                "total": res.data.total
            };
        }
    });
}

//检索数据
function queryForm() {
    $("#day-line-table").bootstrapTable('refresh');
}

//收集查询参数
function collectForm() {
    var data = {};
    var t = $('#dayLineForm').serializeArray();
    $.each(t, function() {
        if(!empty(this.value)){
            data[this.name] = this.value;
        };
    });
    return data;
}

// 操作按钮
function btnGroup() {
    var operations =
        '<a href="#!" class="btn btn-xs btn-default m-r-5 edit-btn" title="编辑"><i class="mdi mdi-pencil"></i></a>' +
        '<a href="#!" class="btn btn-xs btn-default m-r-5 del-btn" title="删除"><i class="mdi mdi-window-close"></i></a>' +
        '<a href="#!" class="btn btn-xs btn-default modal-btn" title="K图"><i class="mdi mdi-chart-line"></i></a>';
    return operations;
}

// 操作方法 - 编辑
function editEvent(row) {
    alert('跳转修改信息');
}

// 操作方法 - 删除
function delEvent(row) {
    alert('信息删除成功');
}

//模态窗口打开时间
function modalEvent(row){
    $('#myLargeModal').on('shown.bs.modal', function () {
        klineData(row.tsCode);
    })
    $('#myLargeModal').modal("show");
}

function klineData(tsCode) {
    $.ajax({
        type: 'post',
        url: '/day-line/line/'+tsCode,
        dataType: 'json',
        contentType: "application/json",
        success: function (result) {
            if(result.code == 401){
                return;
            }
            if(result.code == 200){
                var kdatas = splitData(result.data.lines);
                lineChartInit(kdatas);
            }
        },
        error: function (e) {
            console.log(e);
            return;
        },
        beforeSend: function (XMLHttpRequest) {
            XMLHttpRequest.setRequestHeader("Access_Token", sessionStorage.getItem("token"));
        }
    })
}

function splitData(rawData) {
    var categoryData = [];
    var values = []
    for (var i = 0; i < rawData.length; i++) {
        categoryData.push(rawData[i].splice(0, 1)[0]);
        values.push(rawData[i])
    }
    return {
        categoryData: categoryData,
        values: values
    };
}

function calculateMA(dayCount,kdatas) {
    var result = [];
    for (var i = 0, len = kdatas.values.length; i < len; i++) {
        if (i < dayCount) {
            result.push('-');
            continue;
        }
        var sum = 0;
        for (var j = 0; j < dayCount; j++) {
            sum += kdatas.values[i - j][1];
        }
        result.push(sum / dayCount);
    }
    return result;
}

function lineChartInit(kdatas) {
    var upColor = '#ec0000';
    var upBorderColor = '#8A0000';
    var downColor = '#00da3c';
    var downBorderColor = '#008F28';
    var option = {
        title: {
            text: '上证指数',
            left: 0
        },
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            }
        },
        legend: {
            data: ['日K', 'MA5', 'MA10', 'MA20', 'MA30']
        },
        grid: {
            left: '10%',
            right: '10%',
            bottom: '15%'
        },
        xAxis: {
            type: 'category',
            data: kdatas.categoryData,
            scale: true,
            boundaryGap: false,
            axisLine: {onZero: false},
            splitLine: {show: false},
            splitNumber: 20,
            min: 'dataMin',
            max: 'dataMax'
        },
        yAxis: {
            scale: true,
            splitArea: {
                show: true
            }
        },
        dataZoom: [
            {
                type: 'inside',
                start: 50,
                end: 100
            },
            {
                show: true,
                type: 'slider',
                top: '90%',
                start: 50,
                end: 100
            }
        ],
        series: [
            {
                name: '日K',
                type: 'candlestick',
                data: kdatas.values,
                itemStyle: {
                    color: upColor,
                    color0: downColor,
                    borderColor: upBorderColor,
                    borderColor0: downBorderColor
                },
                markPoint: {
                    label: {
                        normal: {
                            formatter: function (param) {
                                return param != null ? Math.round(param.value) : '';
                            }
                        }
                    },
                    data: [
                        {
                            name: 'XX标点',
                            coord: ['20130531', 2300],
                            value: 2300,
                            itemStyle: {
                                color: 'rgb(41,60,85)'
                            }
                        },
                        {
                            name: 'highest value',
                            type: 'max',
                            valueDim: 'highest'
                        },
                        {
                            name: 'lowest value',
                            type: 'min',
                            valueDim: 'lowest'
                        },
                        {
                            name: 'average value on close',
                            type: 'average',
                            valueDim: 'close'
                        }
                    ],
                    tooltip: {
                        formatter: function (param) {
                            alert(param.name)
                            alert(param.data.coord)
                            return param.name + '<br>' + (param.data.coord || '');
                        }
                    }
                },
                markLine: {
                    symbol: ['none', 'none'],
                    data: [
                        [
                            {
                                name: 'from lowest to highest',
                                type: 'min',
                                valueDim: 'lowest',
                                symbol: 'circle',
                                symbolSize: 10,
                                label: {
                                    show: false
                                },
                                emphasis: {
                                    label: {
                                        show: false
                                    }
                                }
                            },
                            {
                                type: 'max',
                                valueDim: 'highest',
                                symbol: 'circle',
                                symbolSize: 10,
                                label: {
                                    show: false
                                },
                                emphasis: {
                                    label: {
                                        show: false
                                    }
                                }
                            }
                        ],
                        {
                            name: 'min line on close',
                            type: 'min',
                            valueDim: 'close'
                        },
                        {
                            name: 'max line on close',
                            type: 'max',
                            valueDim: 'close'
                        }
                    ]
                }
            },
            {
                name: 'MA5',
                type: 'line',
                data: calculateMA(5,kdatas),
                smooth: true,
                lineStyle: {
                    opacity: 0.5
                }
            },
            {
                name: 'MA10',
                type: 'line',
                data: calculateMA(10,kdatas),
                smooth: true,
                lineStyle: {
                    opacity: 0.5
                }
            },
            {
                name: 'MA20',
                type: 'line',
                data: calculateMA(20,kdatas),
                smooth: true,
                lineStyle: {
                    opacity: 0.5
                }
            },
            {
                name: 'MA30',
                type: 'line',
                data: calculateMA(30,kdatas),
                smooth: true,
                lineStyle: {
                    opacity: 0.5
                }
            }
        ]
    };

    var klineChart = echarts.init(document.getElementById('kline-echart'));
    klineChart.clear();
    // 使用刚指定的配置项和数据显示图表。
    klineChart.setOption(option);
    var chartWidth = $('#kline-echart').width()+'px';
    var chartHeight = $('#kline-echart').height()+'px';
    klineChart.resize({width: chartWidth,height: chartHeight});
}