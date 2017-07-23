# spring-data-events

An implementation of missing spring data repository hooks.
JPA @EventHandlers have following limitations, which
have been solved in this library:
1) It leads to circular dependency between your entity and
it's event handler which makes it impossible to split your
application into dedicated modules or extends an existing
one with own modules.
2) The inability to inject spring beans without using of
static context.
