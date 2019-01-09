# myFramwork

### 异步库

```java
JerryTask.with(context).assign(new BackgroundTask() {
    @Override
    public Object onBackground() {
        return null;
    }
}).whenBroken(new WhenTaskBroken() {
    @Override
    public void whenBroken(final Throwable t) {

    }
}).whenBroken(new WhenTaskBroken() {
    @Override
    public void whenBroken(final Throwable t) {

    }
}).execute();
```
