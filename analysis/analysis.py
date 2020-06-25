from IPython.display import Markdown, display, clear_output
import matplotlib.pyplot as plt
import ipywidgets as widgets
import pandas as pd
import glob
import os

# Default stype
plt.style.use('grayscale')
plt.rc('figure', facecolor='white')

# Metric types ----------------------------------------------------------------

class Counter:
    def __init__(self, df):
        self.data = df
        
    def plot_counters(self):
        return self.data.plot(
            title = kwargs.get('title'),
               ax = kwargs.get('ax')
        )

class Histogram:
    def __init__(self, df):
        df['s1_rate'] = df['count'] - df['count'].shift()
        df['s1_rate'] = df['s1_rate'].fillna(0)
        df['s1_rate'] = df['s1_rate'].astype('int64')
        df = df[df['s1_rate'] > 0] # discard setup and teardown
        self.data = df
        
    def plot_rate(self, **kwargs):
        ax = self.data.plot(
             title = kwargs.get('title'), 
                ax = kwargs.get('ax'),
                 y = ['m1_rate', 's1_rate'],
             style = ['-', '.']
        )
        ax.legend(['1m rate', '1s rate'], ncol=2)
        return ax

class Timer:
    def __init__(self, df):
        self.hist = Histogram(df)
        
    @property
    def data(self):
        return self.hist.data
                
    def plot_rate(self, **kwargs):
        return self.hist.plot_rate(**kwargs)
        
    def plot_latencies(self, **kwargs):
        ax = self.data.plot(
            title = kwargs.get('title'), 
               ax = kwargs.get('ax'),
                y = ['p99', 'p95', 'p75', 'p50']
        )
        ax.legend(ncol=4)
        return ax
    
class Stacked:
    def __init__(self, a, b, column):
        self.data = pd.DataFrame()
        self.data['a'] = a.data[column]
        self.data['b'] = b.data[column]
        self.data['percent'] = \
            (self.data['a'] / self.data['b']).fillna(0) * 100
        
    def plot_stack(self, **kwargs):
        return self.data.plot.area(
             title  = kwargs.get('title'),
                ax  = kwargs.get('ax'),
                 y  = ['a', 'b'],
            stacked = False,
            legend  = False
        )
        
    def plot_percent(self, **kwargs):
        return self.data.plot(
             title = kwargs.get('title'), 
                ax = kwargs.get('ax'),
                 y = 'percent',
            legend = False
        )

# Parsing functions -----------------------------------------------------------
    
def read_csv(file):
    try:
        df = pd.read_csv(file)
        df = df.fillna(0)
        df['t'] = pd.to_datetime(df['t'], unit='s')
        df = df.set_index('t')
        df = df.loc[~df.index.duplicated(keep='first')] # rare but possible
        return df
    except FileNotFoundError:
        return None

def read(wrapper, file):
    df = read_csv(file)
    if df is not None:
        return wrapper(df)
    return None

# Plot functions --------------------------------------------------------------

def plot(plot_fn, **kwargs):
    ax = plot_fn(**kwargs)
    ax.set_xlabel(kwargs.get('xlabel'))
    ax.set_ylabel(kwargs.get('ylabel'))
    if 'legend' in kwargs:
        legend = kwargs['legend']
        ax.legend(legend, ncol=len(legend))
    
def plot_2columns(lhs, rhs, **kwargs):
    def prefixed(prefix, args):
        res = {}
        for (key, value) in args.items():
            if key.startswith(prefix):
                key = key[len(prefix):]
                res[key] = value
        return res

    fig = None
    if 'axs' not in kwargs:
        fig, (axl, axr) = plt.subplots(ncols=2, figsize=(14, 4))
    else:
        (axl, axr) = kwargs['axs']    
    plot(lhs, **prefixed('l_', kwargs), ax=axl)
    plot(rhs, **prefixed('r_', kwargs), ax=axr)
    if fig is not None:
        fig.suptitle(kwargs.get('suptitle'))
        plt.tight_layout()


# Single experiment plots -----------------------------------------------------

class Experiment:
    BASE_PATH = 'tmp/parallel-SMR/metrics'
    CLIENT_RPS_PATH = BASE_PATH + '/demo.dict.DictClient.requests.csv'    
    SERVER_CBASE_REQUESTS_PATH = BASE_PATH + '/parallelism.late.CBASEServiceReplica.requests.csv'
    SERVER_CBASE_COMMANDS_PATH = BASE_PATH + '/parallelism.late.CBASEServiceReplica.commands.csv'
    SERVER_COS_GRAPH_SIZE = BASE_PATH + '/parallelism.late.graph.COS.size.csv'
    SERVER_COS_GRAPH_READY = BASE_PATH + '/parallelism.late.graph.COS.ready.csv'
    SERVER_POOLED_REQUESTS_PATH = BASE_PATH + '/parallelism.pooled.PooledScheduler.requests.csv'
    SERVER_POOLED_COMMANDS_PATH = BASE_PATH + '/parallelism.pooled.PooledScheduler.commands.csv'
    SERVER_POOLED_GRAPH_SIZE = BASE_PATH + '/parallelism.pooled.PooledScheduler.size.csv'
    SERVER_POOLED_GRAPH_READY = BASE_PATH + '/parallelism.pooled.PooledScheduler.ready.csv'
    
    def __init__(self, path):
        def parse_int(text, size=3):
            if size == 0: return None
            try: return int(text[:size])
            except: return parse_int(text, size-1)
            
        parts = os.path.basename(os.path.normpath(path)).split('-')
        self.server_threads = parse_int(parts[0])
        self.client_threads = parse_int(parts[1])
        self.sparseness = parse_int(parts[2])
        self.conflict = parse_int(parts[3])
        self.ops_per_request = parse_int(parts[4])
        self.workload = parse_int(parts[5])
        self.op_cost_ms = parse_int(parts[6]) if len(parts) >= 7 else None
        self.scheduler_type = parts[7] if len(parts) >= 8 else None
        self.path = path
        
    def filename(self, *parts):
        return os.path.join(self.path, *parts)
    
    def client_thoughput(self, node):
        return read(Timer, self.filename(node, self.CLIENT_RPS_PATH))
    
    def server_request_throughput(self, node):
        cbase = self.filename(node, self.SERVER_CBASE_COMMANDS_PATH)
        pooled = self.filename(node, self.SERVER_POOLED_REQUESTS_PATH)
        return read(Histogram, cbase) or read(Histogram, pooled)
    
    def server_commands_throughtput(self, node):
        cbase = self.filename(node, self.SERVER_CBASE_COMMANDS_PATH)
        pooled = self.filename(node, self.SERVER_POOLED_COMMANDS_PATH)
        return read(Histogram, cbase) or read(Histogram, pooled)
        
    def server_graph_size(self, node):
        cbase = self.filename(node, self.SERVER_COS_GRAPH_SIZE)
        pooled = self.filename(node, self.SERVER_POOLED_GRAPH_SIZE)
        return read(Histogram, cbase) or read(Counter, pooled)
        
    def server_graph_ready(self, node):
        cbase = self.filename(node, self.SERVER_COS_GRAPH_READY)
        pooled = self.filename(node, self.SERVER_POOLED_GRAPH_READY)
        return read(Histogram, cbase) or read(Counter, pooled)
    
    def __repr__(self):
        return 'Experiment in: ' + self.path

class ExperimentOverview:
    def __init__(self, path, servers, clients):
        self.experiment = Experiment(path)
        self.servers = servers
        self.clients = clients
        
    def plot_client_throughput(self):
        fig, axs = plt.subplots(
              ncols = 2,
              nrows = len(self.clients), 
            figsize = (14, 4*len(self.clients))
        )
        
        for i in range(len(self.clients)):
            client, ax = self.clients[i], axs[i]
            requests = self.experiment.client_thoughput(client)
            
            plot_2columns(
                requests.plot_rate, 
                requests.plot_latencies,
                 l_title = 'Requests (request/sec)' if i == 0 else None,
                 r_title = 'Latency (ms)' if i == 0 else None,
                l_ylabel = client,
                     axs = ax
            )
            
        fig.suptitle('Client Throughput', fontsize=18)
        fig.tight_layout(rect=[0, 0, 1, 0.97])
        plt.close()
        return fig
        
    def plot_server_throughput(self):
        fig, axs = plt.subplots(
              ncols = 2,
              nrows = len(self.servers),
            figsize = (14, 4*len(self.servers))
        )
        
        for i in range(len(self.servers)):
            server, ax = self.servers[i], axs[i]
            requests = self.experiment.server_request_throughput(server)
            commands = self.experiment.server_commands_throughtput(server)
            
            plot_2columns(
                requests.plot_rate, 
                commands.plot_rate,
                 l_title = 'Requests (request/sec)' if i == 0 else None,
                 r_title = 'Commands (command/ms)' if i == 0 else None,
                l_ylabel = server,
                     axs = ax
            )
            
        fig.suptitle('Server Throughput', fontsize=18)
        fig.tight_layout(rect=[0, 0, 1, 0.97])
        plt.close()
        return fig
        
    def plot_graph_state(self):
        fig, axs = plt.subplots(
              ncols = 2,
              nrows = len(self.servers),
            figsize = (14, 4*len(self.servers))
        )
        
        for i in range(len(self.servers)):
            server, ax = self.servers[i], axs[i]
            size = self.experiment.server_graph_size(server)
            ready = self.experiment.server_graph_ready(server)
            stacked = Stacked(ready, size, 'mean' if isinstance(size, Histogram) else 'count')
            
            plot_2columns(
                stacked.plot_stack,
                stacked.plot_percent,
                 l_title = 'Graph Size vs Ready' if i == 0 else None,
                 r_title = 'Ready / Size %' if i == 0 else None,
                l_legend = ['ready', 'size'],
                l_ylabel = server,
                     axs = ax
            )
            
        fig.suptitle('Graph Population', fontsize=18)
        fig.tight_layout(rect=[0, 0, 1, 0.97])
        plt.close()
        return fig
    
# Consolidated view -----------------------------------------------------------

def consolidate(basepath, servers, filepattern, fn):
    paths = sorted(glob.glob(os.path.join(basepath, filepattern)))
    if len(paths) == 0:
        return None
    data = pd.DataFrame()
    for path in paths:
        experiment = Experiment(path)
        throughput = pd.Series(dtype='int64')
        for server in servers:
            commands = experiment.server_commands_throughtput(server)
            throughput = throughput.combine(
                commands.data['s1_rate'],
                lambda a, b: a + b,
                fill_value=0
            )
        data = fn(data, experiment, throughput) 
    return data

def consolidated_throughput_per_thread_and_scheduler(basepath, servers, filepattern, **kwargs):
    def aggregate(data, experiment, throughput):
        return data.append({
            'threads': experiment.server_threads,
            'scheduler': experiment.scheduler_type,
            'throughput': throughput.mean()
        }, ignore_index=True)
    
    data = consolidate(basepath, servers, filepattern, aggregate)
    if data is None:
        return kwargs.get('ax')
    
    data = data.astype({ 'threads': 'int64' })
    data = data.pivot(index='threads', columns='scheduler', values='throughput')
    ax = data.plot.bar(rot=0, ax=kwargs.get('ax'))
    ax.legend(['Non-pooled', 'Pooled'], loc='upper left')
    ax.set_xlabel('Number of worker threads')
    ax.set_ylabel('Requests / Second')
    return ax

def consolidated_throughput_per_conflict_and_scheduler(basepath, servers, filepattern, **kwargs):
    def aggregate(data, experiment, throughput):
        return data.append({
            'sparseness': experiment.sparseness,
            'scheduler': experiment.scheduler_type,
            'throughput': throughput.mean()
        }, ignore_index=True)
    
    data = consolidate(basepath, servers, filepattern, aggregate)
    if data is None:
        return kwargs.get('ax')
    
    data = data.astype({ 'sparseness': 'int64' })
    data = data.pivot(index='sparseness', columns='scheduler', values='throughput')
    ax = data.plot.bar(rot=0, ax=kwargs.get('ax'))
    ax.legend(['Non-pooled', 'Pooled'], loc='upper left')
    ax.set_xlabel('Conflict %')
    ax.set_ylabel('Requests / Second')
    return ax

def consolidade_side_by_side(fn, basepath, servers, xlabel, patterns):
    for i in range(len(patterns)):
        pattern, name, ax = patterns[i]
        ax = fn(basepath, servers, pattern, ax=ax)        
        ax.set_xlabel("%s\n%s" % (xlabel, name), fontsize=14)
        
        if i == 0:
            ax.set_ylabel('Requisições por Segundo', fontsize=14)
        else:
            ax.set_ylabel(None)

        if i == len(patterns)-1:
            ax.legend(['Lock-free', 'Java Moderno'], 
                      ncol=2, 
                      fontsize=14,
                      bbox_to_anchor=(1, 1.15))
        else:
            ax.legend().remove()
    
# UI --------------------------------------------------------------------------

def single_experiment_overview(results_path, servers, clients):
    dates = widgets.Dropdown(description = 'Date:')
    experiments = widgets.Dropdown(description = 'Experiment:')
    output = widgets.Output()

    def show_experiment(path):
        with output:
            clear_output(wait=True)
            display(Markdown('**Path:** ' + path))
            experiment = ExperimentOverview(path, servers, clients)
            display(experiment.plot_client_throughput())
            display(experiment.plot_server_throughput())
            display(experiment.plot_graph_state())

    def refresh_experiments(path):
        experiments.options = sorted(glob.glob(path + '/*'))

    display(dates)
    display(experiments)
    display(output)

    experiments.observe(lambda evt: show_experiment(evt.owner.value), names='value')
    dates.observe(lambda evt: refresh_experiments(evt.owner.value), names = 'value')
    dates.options = sorted(glob.glob(results_path + '/*'))