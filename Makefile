include Configfile

.DEFAULT: build
.PHONY: build

build: 
	./gradlew build

image: build
	docker build -t $(IMAGE_NAME) .

release:
	docker tag $(IMAGE_NAME) $(IMAGE_REPO)/$(IMAGE_NAME):$(RELEASE_TAG)
	docker push $(IMAGE_REPO)/$(IMAGE_NAME):$(RELEASE_TAG)