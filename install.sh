#!/bin/bash

function main {
    local schome=~/.local/share/SuperCollider
    local scextensions=${schome}/Extensions
    [ ! -d $scextensions ] && mkdir -pv $scextensions
    cp -v classes/*.sc $scextensions
}

main "$@"
