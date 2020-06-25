# Parallel State Machine Replication (SMR)

Este projeto é um fork de https://github.com/parallel-SMR/library, utilizado no
na pesquisa documentada em [Artigo.pdf](Artigo.pdf). Para mais informações,
consulte o projeto original.

Neste fork foram desenvolvidos:
- Uma nova carga de trabalho baseada em um dicionário chave-valor;
- Um algoritmo análogo ao algoritmo lock-free desenvolvido pelos autores
  originais.

Vide os pacotes `src/demo/dict` e `src/parallelism/pooled` para suas respectivas
implementações.

# Experimentos

**Requer**: Ansible 2.9.+

O diretório `ops` contém os scripts Ansible necessários para execução dos
experimentos. Verifique a configuração do arquivo `ops/hosts`. Os scripts
assumem três máquinas-clientes e três máquinas-servidoras, todas com máquina
virtual Java versão 11+ instalada.

Uma vez configurados as máquinas, execute o script `ops/run.sh` para iniciar os
experimentos.

# Resultados

**Requer**:
- jupyter lab: 2.1.+
- ipywidgets: 7.5.+

Os dados coletados durante a pesquisa se encontram em `ops/results/`. O
diretório `analysis` contém os notebooks e scripts de extração de dados. Os
notebooks são auto-explicativos. Abra-os com a ferramenta Jupyter Lab e siga as
instruções.
