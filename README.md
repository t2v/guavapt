# Guavapt

Guavapt is [Guava Functional Idioms](http://code.google.com/p/guava-libraries/wiki/FunctionalExplained) support with APT.

## Usage

Add `@Guavapt` annotation into your bean class.

```java
package my.cool.lib;

import java.util.List;
import jp.t2v.guavapt.Guavapt;

@Guavapt
public class Company {

    private final int id;
    private final String name;
    private final List<Employee> employees;
    private final boolean listed;

    public Company(int id, String name, List<Employee> employees, boolean listed) {
        this.id = id;
        this.name = name;
        this.employees = employees;
        this.listed = listed;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public List<Employee> getEmployees() { return employees; }
    public boolean isListed() { return listed; }
}
```

You can use Functions and Predicates as follows.

```java
  List<Company> companies = ...

  List<Integer> listedCompanyIds = FluentIterable.from(companies)
      .filter(CompanyPredicates.isListed)
      .transform(CompanyFunctions.getId)
      .toList();
```


### files that Guavapt generates.

```java
package my.cool.lib;

import com.google.common.base.Function;

public class CompanyFunctions {
    private CompanyFunctions() {}
    public static final Function<Company, java.lang.Integer> getId = new Function<Company, java.lang.Integer>() {
        @Override
        public java.lang.Integer apply(final Company input) {
            return input.getId();
        }
    };
    public static final Function<Company, java.lang.String> getName = new Function<Company, java.lang.String>() {
        @Override
        public java.lang.String apply(final TestEntity input) {
            return input.getName();
        }
    };
    public static final Function<Company, java.util.List<my.cool.lib.Employee>> getEmployees = new Function<Company, java.util.List<my.cool.lib.Employee>>() {
        @Override
        public java.util.List<my.cool.lib.Employee> apply(final Company input) {
            return input.getEmployees();
        }
    };
    public static final Function<Company, java.lang.Boolean> isListed = new Function<Company, java.lang.Boolean>() {
        @Override
        public java.lang.Boolean apply(final Company input) {
            return input.isListed();
        }
    };
}
```

and

```java
package my.cool.lib;

import com.google.common.base.Predicate;

public class CompanyPredicates {
    private CompanyPredicates() {}
    public static final Predicate<Company> isListed = new Predicate<Company>() {
        @Override
        public boolean apply(final Company input) {
            return input.isListed();
        }
    };
}
```

## Customize

`@Guavapt` has parametes

```java
@Guavapt(functions = true, predicates = false)
```

When functions is true, Guavapt generate functions class.

When predicates is true, Guavapt generate predicates class.


## Customize

You can change class name suffix of generated class.

add `guavapt.f.suffix` or `guavapt.p.suffix` processor options.

for example
```
javac -cp guavapt.jar -s -Aguavapt.f.suffix=Foo -Aguavapt.p.suffix=Bar my/cool/lib/Company.java
```

Gavapt generate `CompanyFoo` and `CompanyBar` insted of `CompanyFunctions` and `CampanyPredicates`
