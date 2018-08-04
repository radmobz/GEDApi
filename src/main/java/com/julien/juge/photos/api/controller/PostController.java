package com.julien.juge.photos.api.controller;

import com.julien.juge.photos.api.dto.DtoBuilder;
import com.julien.juge.photos.api.dto.output.PostDto;
import com.julien.juge.photos.api.entity.EntityBuilder;
import com.julien.juge.photos.api.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import rx.Observable;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/v1/posts")
@Slf4j
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    /**
     * @api {get} /v1/posts Recuperation de tous les posts presents
     * @apiGroup Post
     * @apiName getAllPosts
     * @apiDescription Recupere tous les posts sans disctinctions
     * @apiVersion 1.0.0
     * @apiHeader {String} Authorization Token de l'utilisateur
     *
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * [
     * {
     * "id": "5b02de5310e77a205cee87cc",
     * "createdDate": "2018-05-21T16:00:00.000+0000",
     * "event": {
     *     "startDate" : "2018-05-22T18:00:00.000+0000",
     *     "place" : "Minute Blonde",
     *     }
     * },
     * {
     * "id": "5b041db7f83b362d545bee0b",
     * "createdDate": "2018-05-21T17:00:00.000+0000",
     * "text": {
     *     "text" : "Un super post avec que du texte"
     *     }
     * }
     * ]
     */
    @RequestMapping(
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<List<PostDto>>> getAllPosts() {
        return postService.getAllPost().map(DtoBuilder::buildListPostDtoOutput).map(ResponseEntity::ok).doOnError(e -> log.error("Erreur de recuperation des posts", e));
    }

    /**
     * @api {post} /v1/posts Sauvegarde d'un post
     * @apiGroup Post
     * @apiName savePost
     * @apiDescription Permet de sauvegarder un post (event, text)
     * @apiVersion 1.0.0
     * @apiHeader {String} Authorization Token de l'utilisateur
     * @apiParam {Date} createdDate Date de creation
     * @apiParam (Text) {String} text Texte du post
     * @apiParam (Event) {Date} startDate Date de debut de l'evenement
     * @apiParam (Event) {Date} [endDate] Date de fin de l'evenement
     * @apiParam (Event) {String} [comment] Commentaire de l'evenement
     * @apiParam (Event) {String} [price] Prix de l'evenement
     * @apiParam (Event) {String} place Emplacement de l'evenement
     * @apiParamExample {json} Request-Example:
     * {
     * "createdDate": "2018-05-22T18:00:00",
     * "text": {
     *     "text": "Un super post !!!!!!"
     * }
     * }
     * @apiParamExample {json} Minimal-Event-Request-Example:
     * {
     * "createdDate": "2018-05-22T18:00:00",
     * "event": {
     *     "startDate": "2018-05-22T18:00:00",
     *     "place": "Minute Blonde"
     * }
     * }
     * @apiSuccessExample {json} Success-Response:
     * HTTP/1.1 200 OK
     * {
     * "success": 1
     * }
     */
    @RequestMapping(
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE
    )
    public Observable<ResponseEntity<PostDto>> savePost(@RequestBody @Valid com.julien.juge.photos.api.dto.input.PostDto postInput, Errors errors) {
        return postService.savePost(EntityBuilder.buildPostEntity(postInput)).map(DtoBuilder::buildPostDtoOutput).map(ResponseEntity::ok).doOnError(e -> log.error("Probleme de sauvegarde du post", e));
    }

}
