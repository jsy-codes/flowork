package com.flowork.flowork.domain.contribution.controller;

import com.flowork.flowork.domain.contribution.dto.ContributionResponse;
import com.flowork.flowork.domain.contribution.dto.RoomHealthResponse;
import com.flowork.flowork.domain.contribution.service.ContributionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rooms/{roomId}")
@RequiredArgsConstructor
public class ContirubutionController {
    private final ContributionService contributionService;

    /** 멤버별 기여 breakdown */
    @GetMapping("/contributions")
    public ResponseEntity<List<ContributionResponse>> getContributions(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(contributionService.getContributions(roomId));
    }

    /** 팀 건강도 */
    @GetMapping("/health")
    public ResponseEntity<RoomHealthResponse> getRoomHealth(
            @PathVariable Long roomId) {
        return ResponseEntity.ok(contributionService.getRoomHealth(roomId));
    }
    /** CSV Export */
    @GetMapping("/contributions/export")
    public ResponseEntity<byte[]> exportContributions(@PathVariable Long roomId) {
        byte[] csv = contributionService.exportContributionsCsv(roomId);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=\"room-" + roomId + "-contributions.csv\"")
                .contentType(org.springframework.http.MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(csv);
    }
}
