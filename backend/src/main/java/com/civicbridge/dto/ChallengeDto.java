package com.civicbridge.dto;

import java.util.List;

public class ChallengeDto {

    public record CreateChallengeRequest(
        String title,
        String dept,
        String category,
        String deadline,
        String description,
        String status,
        List<String> tags
    ) {}

    public record UpdateChallengeRequest(
        String title,
        String dept,
        String category,
        String deadline,
        String description,
        String status,
        List<String> tags
    ) {}
}
