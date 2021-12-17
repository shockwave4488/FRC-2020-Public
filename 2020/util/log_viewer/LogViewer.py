import sys
import os
import webbrowser
import dash
import dash_core_components as dcc
import dash_html_components as html
import plotly.graph_objs as go
from dash.dependencies import Input, Output

def scan(directory):
    files = []
    for file in os.listdir(directory):
        if file.endswith(".txt"):
            files.append(file.split(".")[0])

    return files

def read(path):
    file = open(path, 'r')
    raw = file.read()
    file.close()
    lines = raw.split("\n")

    x = []
    y = []
    for line in lines:
        try:
            parts = line.split()
            x.append(float(parts[0]))
            y.append(float(parts[1]))
        except IndexError:
            pass

    return [x,y]

def read_main(path):
    file = open(path, 'r')
    raw = file.read()
    file.close()
    lines = raw.split("\n")

    timestamps = []
    keys = []
    for line in lines:
        try:
            timestamps.append(float(line.split()[0]))
        except ValueError:
            pass
        except IndexError:
            timestamps.append(timestamps[-1])
        key = ""
        for word in line.split()[1:]:
            key += word + " "
        keys.append(key)

    return [timestamps, keys]

def build_graph(name, files):
    graph = html.Div(
        id="graph-root-" + name,
        className="graph-root",
        children=[
            html.Div(
                id="graph-container-" + name,
                className="graph-container",
                style={
                    "width":"80%",
                    "float":"left"
                },
                children=[
                    dcc.Graph(
                        id="graph-" + name,
                        className=name,
                        figure={
                            "data": [],
                            "layout": {
                                "title": name
                            }
                        }
                    )
                ]
            ),
            html.Div(
                id="graph-options-" + name,
                className="graph-options",
                style={
                    "marginLeft":"85%"
                },
                children=[
                    dcc.Checklist(
                        id="options-" + name,
                        options=[{"label":i, "value":i} for i in files],
                        values=[],
                        style={"paddingTop":"50px"},
                        labelStyle={"display":"inline-block", "width": "100%", "marginTop":"8px"}
                    )
                ]
            ),
            html.Div(style={"clear":"both"})
        ]
    )

    return graph

app = dash.Dash()
app.config['suppress_callback_exceptions']=True

directory = sys.argv[1]
files = scan(directory)
name = directory.split("/")[-2 if directory.endswith("/") else -1]
graph = build_graph(name + "1", files)

app.layout = html.Div(
    id="root",
    children=[
        html.Div(
            id="graphs",
            children=[
                graph
            ]
        ),
        html.Div(
            style={"marginBottom": "20px"},
            children=[
                html.Button("Add Graph", id="add-graph", style={"marginLeft": "45%"})
            ]
        )
    ]
)

for i in range(12):
    @app.callback(
        Output("graph-" + name + str(i), "figure"),
        [Input("options-" + name + str(i), "values"),
         Input("graph-" + name + str(i), "className")])
    def update_graph(checked_values, actual_name):
        data = []
        for checked_value in checked_values:
            if directory.endswith("/"):
                path = directory + checked_value + ".txt"
            else:
               path = directory + "/" + checked_value + ".txt"
            
            if checked_value == "main":
                main_data = read_main(path)
                data.append(
                    go.Scatter(
                        x=main_data[0],
                        y=[0 for i in main_data[0]],
                        text=main_data[1],
                        mode="markers",
                        marker={
                            "size": 10,
                            "line": {"width": 0, "color": "white"}
                        },
                        name="main"
                    )
                )
            else:
                line_data = read(path)
                data.append(
                    go.Scatter(
                        x=line_data[0],
                        y=line_data[1],
                        mode="lines+markers",
                        name=checked_value,
                        line={
                            "shape":"linear"
                        }
                    )
                )
        return {
            "data": data,
            "layout": {
                "title": actual_name
            }
        }

@app.callback(
    Output("graphs", "children"),
    [Input("add-graph", "n_clicks")])
def add_graph(clicks):
    graphs = app.layout.children[0].children
    if clicks is None:
        return graphs
    if clicks == 10:
        graphs.append(html.H3("You can only have up to 10 graphs",style={"marginLeft":"40%"}))
    elif clicks < 10:
        graphs.append(build_graph(name + str(len(graphs) + 1), files))
    return graphs

webbrowser.get("C:/Program Files (x86)/Google/Chrome/Application/chrome.exe %s").open("127.0.0.1:8050")
app.run_server()
