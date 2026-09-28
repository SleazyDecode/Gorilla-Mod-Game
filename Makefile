.PHONY: build debug clean

build:
	./scripts/build.sh

debug:
	./scripts/build.sh

clean:
	rm -rf app/build
