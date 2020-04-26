package com.nicefish.cms.service.impl;

import com.nicefish.cms.jpa.entity.PostEntity;
import com.nicefish.cms.jpa.repository.IPostRepository;
import com.nicefish.cms.service.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Transactional
@Service
public class PostServiceImpl implements IPostService {
    @Autowired
    private IPostRepository postRepository;

    @Override
    //利用@Cacheable对方法进行缓存
    //value="posts"，声明一个名为posts的缓存
    //key="T(String).valueOf(#var1.pageNumber).concat('-').concat(#var1.pageSize)"按照var1.pageNumber-var1.pageSize的方式缓存
    //unless="#result==null"申明结果集为空则不缓存
    //例子1-10
    @Cacheable(value="posts",key ="T(String).valueOf(#var1.pageNumber).concat('-').concat(#var1.pageSize)", unless="#result==null")
    public Page getPostsPaging(Pageable var1) {
        return postRepository.findAll(var1);
    }

    @Override
    @Cacheable(value = "post-detail",key = "T(String).valueOf(#id)", unless="#result==null")
    public PostEntity getOne(Long postId){
        return this.postRepository.findDistinctByPostId(postId);
    }

    //TODO:重新设计posts这个缓存的KV结构，避免清除整个缓存
    @Override
    //利用@CacheEvict清除缓存
    //存储过后，清空整个缓存
    @CacheEvict(value = "posts",allEntries = true)
    public PostEntity savePost(PostEntity postEntity) {
        return postRepository.save(postEntity);
    }

    @Override
    @CacheEvict(value = "posts",allEntries = true)
    public Integer delPost(Long postId) {
        return this.postRepository.deleteByPostId(postId);
    }

    @Override
    public Long countByUserId(Long userId) {
        return postRepository.countByUserId(userId);
    }

    @Override
    public Page<PostEntity> getPostsByUserIdAndPaging(Long userId,Pageable pageable) {
        return postRepository.findAllByUserId(userId,pageable);
    }
}
