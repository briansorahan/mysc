#!/bin/bash

function main {
    local schome=~/.local/share/SuperCollider
    local scextensions=${schome}/Extensions
    [ ! -d $scextensions ] && mkdir -p $scextensions
    cp classes/*.sc $scextensions
}

main "$@"
