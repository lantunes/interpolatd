# Interpolatd
-------------

Interpolatd is a string interpolation library for Java. It aspires to be easy to
use and yet flexible and powerful. It is string interpolation done right.

## Getting Started
------------------

Download [the latest .jar](http://repository.sonatype.org/service/local/artifact/maven/redirect?r=central-proxy&g=org.bigtesting&a=interpolatd&v=LATEST).
Or, add the following dependency to your pom.xml:

```xml
<dependency>
    <groupId>org.bigtesting</groupId>
    <artifactId>interpolatd</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Usage
--------

There is a single class of interest in the library: **Interpolator**. It exposes
a fluent interface for configuring interpolations. Normally, you'll create
a single instance of this class in your application. It is thread-safe.

First, you create an instance of an Interpolator:

```java
Interpolator<String> interpolator = new Interpolator<String>();
```

The Interpolator class is generic. The type parameter is the type of 
argument (in this case a String) expected in the Interpolator's *interpolate()*
method, as you'll see next.

Next, you configure the Interpolator:

```java
interpolator.when().enclosedBy("#{").and("}")
    .handleWith(new Substitutor<String>() {
        public String substitute(String captured, String arg) {
            return arg;
        }
    });
```

In the snippet above, we are configuring the Interpolator such that it
will substitute anything enclosed by *#{* and *}*. The *handleWith()* method
accepts a **Substitutor**. The Substitutor is also generic, and its 
type parameter must match the Interpolator's type parameter. In the
Subsitutor's *subsitute()* method, the *captured* argument is the value
captured by the pattern. In the example above, this will be the value
between the *#{* and *}*.

Next, you call the *interpolate()* method of the Interpolator:

```java
interpolator.interpolate("Hello #{name}!", "World");
//returns "Hello World!"
```

You can also substitute values prefixed by a specific String:

```java
interpolator.when().prefixedBy(":")
    .handleWith(new Substitutor<String>() {
        public String substitute(String captured, String arg) {
            return arg;
        }
    });
```

The Interpolator will now interpolate a String like *"Hello :name!"*.

There's no reason we need to be limited to one pattern or another.
With the Interpolator configured as above, we can interpolate Strings
containing both patterns:

```java
interpolator.interpolate("Hello :name #{name}!", "foo");
//returns "Hello foo foo!"
```

In practice, you will not be passing along a String as an argument to the 
*interpolate()* method. You will likely be using a Map, or some other more
application-specific type, to return more meaningful values, based on the 
value of the captured argument.

### Character Classes

If you want to place restrictions on what can be prefixed or enclosed, you 
can specify your own character classes while configuring the Interpolator:

```java
Interpolator<String[]> interpolator = new Interpolator<String[]>();
        
interpolator.when("[0-9]").enclosedBy("#{").and("}")
    .handleWith(new Substitutor<String[]>() {
        public String substitute(String captured, String[] arg) {
            return arg[Integer.valueOf(captured)];
        }
    });
```

In the configuration above, we are saying that only single digits
from 0-9 that are enclosed by *#{* and *}* should be substituted.
We can then use the Interpolator as follows:

```java
interpolator.interpolate("Hello #{0}, #{1}, but not #{you}.", 
    new String[]{"John", "Jane"});
//returns "Hello John, Jane, but not #{you}."
```

We could also have specified that any number of digits from 0-9
be matched, by using *"[0-9]+"* as an argument, etc.

### Escaping Substitution Patterns

At times, you may want to escape substitution patterns. The 
Interpolator will not substitute patterns if there is no value to 
substitute with. For example, if the Substitutor returns null when 
asked to handle *"Hello #{name}"*, the interpolation result will 
be *"Hello #{name}"*. However, if you want to explicitly escape a 
substitution pattern because the Substitutor may return a value, 
you can specify an escape character or sequence:

```java
Interpolator<String> interpolator = new Interpolator<String>();
        
interpolator.when().enclosedBy("#{").and("}")
    .handleWith(new Substitutor<String>() {
        public String substitute(String captured, String arg) {
            return arg;
        }
    });

interpolator.escapeWith("^");
```

In the snippet above, we are specifying *^* as an escape character.
There can only be one. On their own, escape characters are treated
like any other character. However, if they are placed next to a 
substitution pattern, they act as genuine escape characters:

```java
interpolator.interpolate("Hello #{name}, but not ^#{you} or ^.", "World");
//returns "Hello World, but not #{you} or ^."
```

If you want to produce an escape character next to a substitution 
pattern, you can escape the escape. For example:

```java
interpolator.interpolate("Hello ^^ ^^#{name}", "World");
//returns "Hello ^^ ^World"
``` 

Notice that the escape characters on their own--that are not next to a 
substitution pattern--are not treated as escape characters.









