#!/usr/bin/env bash

set -x -e

for workload in "1000" "10000" "100000"
do
    for server_threads in "1" "2" "4" "8" "16" "32" "64"
    do
        for scheduler_type in "NON_POOLED" "POOLED"
        do
            formated_threads="$(printf '%03d' "$server_threads")"
            formated_workload="$(echo "$workload / 1000" | bc)"
            formated_workload="$(printf '%03.f' "$formated_workload")"
            results_path="results/final/${formated_threads}server-1client-35sparseness-000conflict-50ops-${formated_workload}k-1ms-${scheduler_type}"

            if [ ! -d "$results_path" ]
            then
                time ansible-playbook -i hosts run_experiment.yaml \
                    -e keys="$workload" \
                    -e server_threads="$server_threads" \
                    -e scheduler_type="$scheduler_type" \
                    -e conflict_percent="0" \
                    -e results_path="$results_path" \
                    -t run,stop,fetch
            fi
        done
    done
done

for workload in "1000" "10000" "100000"
do
    for conflict_percent in "0" "0.25" "0.5" "0.75" "1"
    do
        for scheduler_type in "NON_POOLED" "POOLED"
        do
            formated_conflict="$(echo "$conflict_percent * 100" | bc)"
            formated_conflict="$(printf '%03.f' "$formated_conflict")"
            formated_workload="$(echo "$workload / 1000" | bc)"
            formated_workload="$(printf '%03.f' "$formated_workload")"
            results_path="results/final/32server-1client-35sparseness-${formated_conflict}conflict-50ops-${formated_workload}k-1ms-${scheduler_type}"

            if [ ! -d "$results_path" ]
            then
                time ansible-playbook -i hosts run_experiment.yaml \
                    -e keys="$workload" \
                    -e server_threads="32" \
                    -e scheduler_type="$scheduler_type" \
                    -e conflict_percent="$conflict_percent" \
                    -e results_path="$results_path" \
                    -t run,stop,fetch
            fi
        done
    done
done
