# Java-GPT

Do not implement, just ask to chat GPT.

## Example usage
```java
public interface Test {
    @Gpt("This method return sum of two integer")
    int sum(int a, int b);

    @Gpt("Sort by age desc")
    List<Person> sort(List<Person> people);

    record Person(
            int age,
            String name
    ) {
    }

    public static void main(String[] args) {
        String apiKey = "Your OpenAI Api Key";
        Test test = JavaGpt.generate(apiKey, Test.class);

        System.out.println(test.sum(1, 2));

        List<Test.Person> people = List.of(
                new Test.Person(20, "Alice"),
                new Test.Person(30, "Bob"),
                new Test.Person(10, "Charlie")
        );
        System.out.println(test.sort(people));
    }
}
```
