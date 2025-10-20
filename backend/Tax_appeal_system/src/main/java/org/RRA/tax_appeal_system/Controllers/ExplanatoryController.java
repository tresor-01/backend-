package org.RRA.tax_appeal_system.Controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.RRA.tax_appeal_system.DTOS.requests.ExplanatoryNoteDTO;
import org.RRA.tax_appeal_system.DTOS.responses.GenericResponse;
import org.RRA.tax_appeal_system.Services.ExplanatoryNoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@Tag(name = " Explanatory Note Related APIs")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth/explanatory_note/")
public class ExplanatoryController {
    private final ExplanatoryNoteService explanatoryNoteService;

    @Operation(summary = " Generating a new Explanatory Note")
    @PostMapping("/")
    public ResponseEntity<GenericResponse<String>> createExplanatoryNote(@RequestBody ExplanatoryNoteDTO explanatoryNote, Principal principal) {
        explanatoryNoteService.generateExplanatoryNote(explanatoryNote,principal.getName());
        GenericResponse<String> response = new GenericResponse<>(
                HttpStatus.CREATED.value(),
                "succesfully Registered new explanatory Note",
                null
        );
         return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = " Get Explanatory note by CaseId")
    @GetMapping("/{caseId}")
    public ResponseEntity<GenericResponse<ExplanatoryNoteDTO>> getExplanatoryNoteByCaseId(
            @PathVariable String caseId) {

            ExplanatoryNoteDTO explanatoryNote = explanatoryNoteService.getExplanatoryNoteByCaseId(caseId);
            return ResponseEntity.ok(new GenericResponse<>(
                    200,
                    "Explanatory note retrieved successfully",
                    explanatoryNote
            ));
    }

}
