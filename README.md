# lein-jelastic

A Leiningen plugin for [Jelastic][1].

## Configuration

To use lein-jelastic add the following configuration to your
`project.clj` file.

```clojure
:plugins [[lein-jelastic "0.1.1"]]

:jelastic {:apihoster "app.jelastic.provider.com"
           :email "your@mail.com"
           :password "XXXXXXXX" 
           :environment "myapp"
           ; Optionals 
           ; :context "mycontext"
           ; Custom filename can be set for example to match ring uberwar output
           ; :custom-filename ~(fn [proj]
           ;                    (str (:name proj) "-" (:version proj) "-STANDALONE")) 
           }
```

As storing user and password information in the project directly is generally not
so good idea, you can store sensitive information in your `~/.lein/profiles.clj` file.

```clojure
{:user {:jelastic {:email "your@email.com" :password "XXXXXXX"}}}
```

## Upload

Upload current target to jelastic

    $ lein jelastic upload
    
## Deploy

Upload and deploy current target

    $ lein jelastic deploy

## License

Copyright © 2013 Mysema

Distributed under the Eclipse Public License, the same as Clojure.

[1]: http://www.jelastic.com
