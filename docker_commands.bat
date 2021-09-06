docker build -f ./Dockerfile1.dockerfile -t my-david ./
docker run -dit --name my-david05 -p 8080:8080 my-david
