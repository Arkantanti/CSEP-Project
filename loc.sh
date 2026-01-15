#!/usr/bin/env bash
set -u

# 1. Check for git repo
if ! git rev-parse --is-inside-work-tree >/dev/null 2>&1; then
    echo "Error: current directory is not a git repository." >&2
    exit 1
fi

# 2. Define path patterns
PATH_CLIENT='client/src/**/*.java'
PATH_SERVER='server/src/**/*.java'
PATH_COMMONS='commons/src/**/*.java'
PATH_PROD='*/src/main/*.java'
PATH_TEST='*/src/test/*.java'
PATH_ALL='*.java'

# 3. Helper functions

# Standard count: Includes imports, respects WS changes
count_lines_standard() {
    git show --format="" --patch "$1" -- "$2" \
    | grep -E '^\+[^+/][^/]+$' \
    | grep -v '+ *[*@]' \
    | wc -l
}

# Strict count: Ignores imports, ignores pure WS changes
count_lines_strict() {
    git show --format="" --patch -w "$1" -- "$2" \
    | grep -E '^\+[^+/][^/]+$' \
    | grep -v 'import' \
    | grep -v '+ *[*@]' \
    | wc -l
}

echo "Analyzing commits on 'main'..." >&2

# 4. Main Loop
# Use %ae for Author Email
git log --no-merges --pretty=format:'%H %ae' main | {
    
    declare -A client_count
    declare -A server_count
    declare -A commons_count
    declare -A prod_count
    declare -A test_count
    declare -A total_count
    declare -A strict_count
    declare -A seen_users

    while read -r hash raw_email; do
        # Normalize email to lowercase
        email=$(echo "$raw_email" | tr '[:upper:]' '[:lower:]')
        seen_users["$email"]=1

        # Run counts (Standard)
        c_add=$(count_lines_standard "$hash" "$PATH_CLIENT")
        s_add=$(count_lines_standard "$hash" "$PATH_SERVER")
        k_add=$(count_lines_standard "$hash" "$PATH_COMMONS")
        p_add=$(count_lines_standard "$hash" "$PATH_PROD")
        t_add=$(count_lines_standard "$hash" "$PATH_TEST")
        all_add=$(count_lines_standard "$hash" "$PATH_ALL")
        
        # Run count (Strict)
        strict_add=$(count_lines_strict "$hash" "$PATH_ALL")

        # Accumulate
        client_count["$email"]=$(( ${client_count["$email"]:-0} + c_add ))
        server_count["$email"]=$(( ${server_count["$email"]:-0} + s_add ))
        commons_count["$email"]=$(( ${commons_count["$email"]:-0} + k_add ))
        prod_count["$email"]=$(( ${prod_count["$email"]:-0} + p_add ))
        test_count["$email"]=$(( ${test_count["$email"]:-0} + t_add ))
        total_count["$email"]=$(( ${total_count["$email"]:-0} + all_add ))
        strict_count["$email"]=$(( ${strict_count["$email"]:-0} + strict_add ))

        printf "." >&2
    done

    echo "" >&2
    echo "Done." >&2

    # 5. Print Table
    # Widths: Email=40, Others=10, Strict=13
    printf "%-40s | %-10s | %-10s | %-10s | %-10s | %-10s | %-10s | %-13s\n" \
        "User Email" "Client" "Server" "Commons" "Prod" "Test" "Total" "Total (Strict)"
    printf "%s\n" "-----------------------------------------|------------|------------|------------|------------|------------|------------|----------------"

    for email in "${!seen_users[@]}"; do
        echo "$email"
    done | sort | while read -r e; do
        printf "%-40s | %-10d | %-10d | %-10d | %-10d | %-10d | %-10d | %-13d\n" \
            "$e" \
            "${client_count[$e]:-0}" \
            "${server_count[$e]:-0}" \
            "${commons_count[$e]:-0}" \
            "${prod_count[$e]:-0}" \
            "${test_count[$e]:-0}" \
            "${total_count[$e]:-0}" \
            "${strict_count[$e]:-0}"
    done
}
