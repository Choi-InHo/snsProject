package com.example.sis.Controller;


import com.example.sis.Controller.request.PostCreateRequest;
import com.example.sis.Controller.request.PostModifyRequest;
import com.example.sis.Controller.response.PostResponse;
import com.example.sis.Controller.response.Response;
import com.example.sis.model.Post;
import com.example.sis.model.User;
import com.example.sis.model.entity.UserEntity;
import com.example.sis.repository.UserEntityRepository;
import com.example.sis.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final UserEntityRepository userEntityRepository;

    @PostMapping
    public Response<Void> create(@RequestBody PostCreateRequest request, Authentication authentication) {
        postService.create(request.getTitle(), request.getBody(), authentication.getName());

        return Response.success();
    }

    @PutMapping("/{postId}")
    public Response<PostResponse> modify(@PathVariable Integer postId , @RequestBody PostModifyRequest request, Authentication authentication) {
        Post post = postService.modify(request.getTitle(), request.getBody(), authentication.getName(),postId);

        return Response.success(PostResponse.fromPost(post));
    }
}
