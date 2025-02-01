//package com.example.gympt.domain.category.controller;
//
//import com.example.gympt.domain.category.dto.LocalDTO;
//import com.example.gympt.domain.category.dto.LocalResponseDTO;
//import com.example.gympt.domain.category.service.LocalService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/local")
//
//public class LocalController {
//
//    private final LocalService localService;
//
//
//    @GetMapping("/list/{localId}")
//    public ResponseEntity<List<LocalResponseDTO>> getCategory(@PathVariable Long localId) {
//        return ResponseEntity.ok(localService.getLocalList(localId));
//    }
//
//    @GetMapping("/list/all")
//    public ResponseEntity<List<LocalDTO>> getAllCategory() {
//        return ResponseEntity.ok(localService.getAll());
//    }
//}
