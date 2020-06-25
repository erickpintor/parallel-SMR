#!/usr/bin/env bash

set -x -e

for server_threads in "1" "2" "4" "8" "16" "32" "64"
do
    for workload in "5000" "50000" "500000"
    do
        for scheduler_type in "NON_POOLED" "POOLED"
        do
            formated_threads="$(printf '%03d' "$server_threads")"
            formated_workload="$(echo "$workload / 1000" | bc)"
            formated_workload="$(printf '%03.f' "$formated_workload")"
            results_path="results/final-a2/${formated_threads}server-001client-100sparseness-000conflict-50ops-001k-${formated_workload}us-${scheduler_type}"

            if [ ! -d "$results_path" ]
            then
                time ansible-playbook -i hosts run_experiment.yaml \
                    -e server_threads="$server_threads" \
                    -e scheduler_type="$scheduler_type" \
                    -e results_path="$results_path" \
                    -e cost_per_op_ns="$workload" \
                    -t run,stop,fetch
            fi
        done
    done
done

for conflict_sd in "0.01" "0.05" "0.1" "0.25" "0.5" "1"
do
    for workload in "5000" "50000" "500000"
    do
        for scheduler_type in "NON_POOLED" "POOLED"
        do
            formated_sd="$(echo "$conflict_sd * 100" | bc)"
            formated_sd="$(printf '%03.f' "$formated_sd")"
            formated_workload="$(echo "$workload / 1000" | bc)"
            formated_workload="$(printf '%03.f' "$formated_workload")"
            results_path="results/final-b2/016server-001client-${formated_sd}sparseness-100conflict-50ops-001k-${formated_workload}us-${scheduler_type}"

            if [ ! -d "$results_path" ]
            then
                time ansible-playbook -i hosts run_experiment.yaml \
                    -e scheduler_type="$scheduler_type" \
                    -e cost_per_op_ns="$workload" \
                    -e key_sparseness="$conflict_sd" \
                    -e results_path="$results_path" \
                    -e server_threads="32" \
                    -e conflict_percent="1" \
                    -t run,stop,fetch
            fi
        done
    done
done

