var colorList = ["#2F0000","#800080","#FF8C00","#00008B","#8B4513","#00FF7F","#FF69B4"];
var klineChart = echarts.init(
    document.getElementById('kline-echart'),
    null,//浅色light，深色dark
    {width: 'auto',height: 'auto'});

//加载列表数据
$(document).ready(function () {
    initTable();
    window.addEventListener("resize", function () {
        this.klineResize();
    });
});

function initTable() {
    $('#rule-line-table').bootstrapTable({
        url: '/rule-line/line-list',
        method: 'post',
        dataType : 'json',
        uniqueId: 'id',
        idField: 'id',
        toolbar: '#toolbar',
        showColumns: false,
        showRefresh: false,
        showToggle: false,
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
            field: 'ruleName',
            title: '规则名称',
        },{
            field: 'nextBreak',
            title: '次日上涨',
        },{
            field: 'operate',
            title: '操作',
            formatter: operateFormatter,
            events: {
                'click .modal-btn': function (event, value, row) {
                    modalEvent(row);
                },
                'click .del-btn': function (event, value, row) {
                    delEvent(row);
                },
                'click .edit-btn': function (event, value, row) {
                    editEvent(row);
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

function operateFormatter() {
    return [
        '<div class="btn-group">',
        '<button title="编辑" type="button" class="btn btn-xs btn-default m-r-5 edit-btn"  singleSelected=true><i class="mdi mdi-pencil"></i></button>',
        '<button title="删除" type="button" class="btn btn-xs btn-default m-r-5 del-btn" singleSelected=true><i class="mdi mdi-window-close"></i></button>',
        '<button title="K图" type="button" class="btn btn-xs btn-default modal-btn" singleSelected=true><i class="mdi mdi-chart-line"></i></button>',
        '</div>'
    ].join('');
}

//检索数据
function queryForm() {
    $("#rule-line-table").bootstrapTable('refresh');
}

//收集查询参数
function collectForm() {
    var data = {};
    var t = $('#ruleLineForm').serializeArray();
    $.each(t, function() {
        if(!empty(this.value)){
            data[this.name] = this.value;
        };
    });
    return data;
}

// 操作方法-编辑
function editEvent(row) {
    alert('跳转修改信息');
}

// 操作方法-删除
function delEvent(row) {
    alert('信息删除成功');
}

//操作方法-K图
function modalEvent(row){
    $('#myLargeModal').on('shown.bs.modal', function () {
        klineData(row.tsCode,row.tradeDate);
    })
    $( '#myLargeModal' ).on( 'hidden.bs.modal' ,function(){
        $("#myLargeModal").unbind("shown.bs.modal");
    });
    $('#myLargeModal').modal("show");
}

function klineData(tsCode,tradeDate) {
    $.ajax({
        type: 'post',
        url: '/rule-line/line/'+tsCode+'/'+tradeDate,
        dataType: 'json',
        contentType: "application/json",
        success: function (result) {
            if(result.code == 401){
                return;
            }
            if(result.code == 200){
                lineChartInit(result);
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
    var values = [];
    var volumns = [];
    for (var i = 0; i < rawData.length; i++) {
        categoryData.push(rawData[i].splice(0, 1)[0]);
        values.push(rawData[i]);
        volumns.push(rawData[i][4]);
    }
    return {
        categoryData: categoryData,
        values: values,
        volumns: volumns
    };
}

function calculateMA(dayCount, data) {
    var result = [];
    for (var i = 0, len = data.values.length; i < len; i++) {
        if (i < dayCount) {
            result.push('-');
            continue;
        }
        var sum = 0;
        for (var j = 0; j < dayCount; j++) {
            sum += data.values[i - j][1];
        }
        result.push(+(sum / dayCount).toFixed(2));
    }
    return result;
}
function lineChartInit(result) {
    var kdatas = splitData(result.data.lines);
    var option = {
        animation: false,
        title: {
            text: result.data.tsCode,
            textStyle: {
                fontSize: 16,
                fontWeight: 'lighter'
            }
        },
        legend: {
            left: 'center',
            data: ['日K', '5', '10', '20', '30', '60', '120', '250']
        },
        color: colorList,
        tooltip: {
            trigger: 'axis',
            axisPointer: {
                type: 'cross'
            },
            backgroundColor: 'rgba(255, 255, 255, 0.8)',
            position: function (pos, params, el, elRect, size) {
                var obj = {top: 10};
                obj[['left', 'right'][+(pos[0] < size.viewSize[0] / 2)]] = 30;
                return obj;
            },
            extraCssText: 'width: 170px'
        },
        axisPointer: {
            link: {xAxisIndex: 'all'},
            label: {
                backgroundColor: '#777'
            }
        },
        toolbox: {
            feature: {
                dataZoom: {
                    yAxisIndex: false
                },
                brush: {
                    type: ['lineX', 'clear']
                }
            }
        },
        brush: {
            xAxisIndex: 'all',
            brushLink: 'all',
            outOfBrush: {
                colorAlpha: 0.1
            }
        },
        grid: [
            {
                left: '10%',
                right: '8%',
                height: '50%'
            },
            {
                left: '10%',
                right: '8%',
                bottom: '20%',
                height: '15%'
            }
        ],
        xAxis: [
            {
                type: 'category',
                data: kdatas.categoryData,
                scale: true,
                boundaryGap: false,
                splitLine: {show: false},
                splitNumber: 20,
                min: 'dataMin',
                max: 'dataMax',
                axisPointer: {
                    z: 100
                },
                axisLine: {
                    onZero: false
                }
            },
            {
                type: 'category',
                gridIndex: 1,
                data: kdatas.categoryData,
                scale: true,
                boundaryGap: false,
                axisLine: {onZero: false},
                axisTick: {show: false},
                splitLine: {show: false},
                axisLabel: {show: false},
                splitNumber: 20,
                min: 'dataMin',
                max: 'dataMax',
                axisPointer: {
                    label: {
                        formatter: function (params) {
                            var seriesValue = (params.seriesData[0] || {}).value;
                            return params.value
                                + (seriesValue != null
                                        ? '\n' + echarts.format.addCommas(seriesValue)
                                        : ''
                                );
                        }
                    }
                }
            }
        ],
        yAxis: [
            {
                scale: true,
                splitArea: {
                    show: true
                }
            },
            {
                scale: true,
                gridIndex: 1,
                splitNumber: 2,
                axisLabel: {show: false},
                axisLine: {show: false},
                axisTick: {show: false},
                splitLine: {show: false}
            }
        ],
        dataZoom: [
            {
                type: 'inside',
                xAxisIndex: [0, 1],
                start: 80,//横轴起始位置
                end: 100
            },
            {
                show: true,
                xAxisIndex: [0, 1],
                type: 'slider',
                top: '85%',
                start: 98,
                end: 100
            }
        ],
        graphic: [{
            type: 'group',
            left: 'center',
            top: 70,
            width: 300,
            bounding: 'raw',
            children: [{
                id: '5',
                type: 'text',
                style: {fill: colorList[0]}
            }, {
                id: '10',
                type: 'text',
                style: {fill: colorList[1]}
            }, {
                id: '20',
                type: 'text',
                style: {fill: colorList[2]}
            }, {
                id: '30',
                type: 'text',
                style: {fill: colorList[3]}
            }, {
                id: '60',
                type: 'text',
                style: {fill: colorList[4]}
            }, {
                id: '120',
                type: 'text',
                style: {fill: colorList[5]}
            }, {
                id: '250',
                type: 'text',
                style: {fill: colorList[6]}
            }]
        }],
        series: [
            {
                name: '日K',
                type: 'candlestick',
                data: kdatas.values,
                itemStyle: {
                    normal: {
                        color: '#FA0000',
                        color0: '#06B800',
                        borderColor: null,
                        borderColor0: null
                    }
                },
                tooltip: {
                    formatter: function (param) {
                        param = param[0];
                        return [
                            'Date: ' + param.name + '<hr size=1 style="margin: 3px 0">',
                            'Open: ' + param.data[0] + '<br/>',
                            'Close: ' + param.data[1] + '<br/>',
                            'Lowest: ' + param.data[2] + '<br/>',
                            'Highest: ' + param.data[3] + '<br/>'
                        ].join('');
                    }
                }
            },
            {
                name: '5',
                type: 'line',
                data: calculateMA(5, kdatas),
                smooth: true,
                showSymbol: false,
                lineStyle: {
                    width: 1
                }
            },
            {
                name: '10',
                type: 'line',
                data: calculateMA(10, kdatas),
                smooth: true,
                showSymbol: false,
                lineStyle: {
                    width: 1
                }
            },
            {
                name: '20',
                type: 'line',
                data: calculateMA(20, kdatas),
                smooth: true,
                showSymbol: false,
                lineStyle: {
                    width: 1
                }
            },
            {
                name: '30',
                type: 'line',
                data: calculateMA(30, kdatas),
                smooth: true,
                showSymbol: false,
                lineStyle: {
                    width: 1
                }
            },
            {
                name: '60',
                type: 'line',
                data: calculateMA(60, kdatas),
                smooth: true,
                showSymbol: false,
                lineStyle: {
                    width: 1
                }
            },
            {
                name: '120',
                type: 'line',
                data: calculateMA(120, kdatas),
                smooth: true,
                showSymbol: false,
                lineStyle: {
                    width: 1
                }
            },
            {
                name: '250',
                type: 'line',
                data: calculateMA(250, kdatas),
                smooth: true,
                showSymbol: false,
                lineStyle: {
                    width: 1
                }
            },
            {
                name: 'Volumn',
                type: 'bar',
                xAxisIndex: 1,
                yAxisIndex: 1,
                data: kdatas.volumns
            }
        ]
    };
    klineChart.clear();
    this.klineResize();
    klineChart.setOption(option);
}

function klineResize() {
    var chartWidth = $('#kline-echart').width()+'px';
    var chartHeight = $('#kline-echart').height()+'px';
    klineChart.resize({width: chartWidth,height: chartHeight});
}