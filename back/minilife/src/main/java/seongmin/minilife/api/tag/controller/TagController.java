package seongmin.minilife.api.tag.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import seongmin.minilife.api.tag.service.TagService;
import seongmin.minilife.common.auth.dto.CustomUserDetails;
import seongmin.minilife.common.response.SuccessResponse;
import seongmin.minilife.domain.tag.dto.RegisterTagReq;

@Tag(name = "태그 API", description = "태그 등록 등..")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {
    private final TagService tagService;

    @Operation(summary = "태그 조회", description = "존재하는 모든 태그 목록 반환")
    @GetMapping("")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> getAllTags() {
        return ResponseEntity.ok().body(SuccessResponse.from(tagService.getAllTags()));
    }

    @Operation(summary = "태그 등록", description = "사용할 태그 등록(Admin만)")
    @PostMapping("/register")
    @PreAuthorize("isAuthenticated() && #userDetails.getRole() == 'ROLE_ADMIN'")
    public ResponseEntity<?> registerTag(@RequestBody RegisterTagReq req,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        tagService.registerTag(req);
        return ResponseEntity.ok().body(SuccessResponse.noContent());
    }

}
