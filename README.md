A Service Fault Tolerance Framework implemented IoC

Launch the `DemoApplication.class`, and trigger GET method
```shell
localhost:8080/hello
```
you will get "Primary Service"

then you can trigger GET method 
```shell
localhost:8080/ft/status
```

then you trigger GET method
```shell
localhost:8080/hello
```
again, you will get "Secondary Service".
