.PHONY: silo

OS=$(shell uname -s)

ifeq ($(OS),Darwin)
SCLANG=/Applications/SuperCollider/SuperCollider.app/Contents//Resources/sclang
endif

ifeq ($(OS),Linux)
SCLANG=/usr/bin/sclang
endif

silo:
	$(SCLANG) scripts/silo.sc
