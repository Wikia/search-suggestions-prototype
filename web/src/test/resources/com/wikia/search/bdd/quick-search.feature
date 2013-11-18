Feature: As I Quick-Search user
  I want to have api to acquire search good quick search functionality

  Scenario: Exact Match Search
    Given following dataset:
      | articleId | title        | redirects  | views | backlinks |
      | 1         | John Price   | John,Price | 1     | 1         |
      | 2         | John Popular |            | 100   | 1         |
    And default endpoint
    When I query for "John"
    Then I want to get "John Price" as first result
    When I query for "Jo"
    Then I want to get "John Popular" as first result


  Scenario: Results are boosted by backlinks
    Given following dataset:
      | articleId | title  | redirects  | views | backlinks |
      | 1         | John 1 | John,Price | 100   | 2         |
      | 2         | John 2 |            | 100   | 1         |
    And default endpoint
    When I query for "Jo"
    Then I want to get "John 1" as first result


  Scenario: Results are boosted by views
    Given following dataset:
      | articleId | title  | redirects  | views | backlinks |
      | 1         | John 1 | John,Price | 1     | 1         |
      | 2         | John 2 |            | 100   | 1         |
    And default endpoint
    When I query for "Jo"
    Then I want to get "John 2" as first result
    And I want to see exactly 2 results

  Scenario: I don't want to get irrelevant results
    Given following dataset:
      | articleId | title  | redirects  |
      | 1         | John 1 | John,Price |
      | 2         | John 2 |            |
    And default endpoint
    When I query for "d"
    Then I want to get empty result set


  Scenario: I want redirects also to be used matched
    Given following dataset:
      | articleId | title  | redirects  |
      | 1         | John 1 | John,Price |
      | 2         | John 2 |            |
    And default endpoint
    When I query for "p"
    Then I want to get "John 1" as first result
    And I want to see exactly 1 result


