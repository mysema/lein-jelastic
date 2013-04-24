# lein-jelastic

A Leiningen plugin for [Jelastic][1].

## Configuration

To use lein-jelastic, you have to add following configuration to your
`project.clj` file.

```clojure
:plugins [[lein-jelastic "0.1.0"]]

:jelastic {:apihoster "app.jelastic.provider.com"
           :email "your@mail.com"
           :password "XXXXXXXX" 
           :environment "myapp"
           ; Optional context
           ; :context "mycontext"
           }
```

## Upload

To upload a current target to jelastic

    $ lein jelastic upload
    
## Deploy

To upload and deploy the current target

    $ lein jelastic deploy

## License

Copyright © 2013 FIXME

Distributed under the Eclipse Public License, the same as Clojure.

[1]: http://www.jelastic.com
