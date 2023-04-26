package com.adivii.companymanagement.data.service;

import org.springframework.stereotype.Service;

import com.adivii.companymanagement.data.entity.Avatar;
import com.adivii.companymanagement.data.repository.AvatarRepository;

@Service
public class AvatarService {
    private AvatarRepository avatarRepository;

    public AvatarService(AvatarRepository avatarRepository) {
        this.avatarRepository = avatarRepository;
    }

    public void saveAvatar(Avatar avatar) {
        if(avatar != null) {
            this.avatarRepository.save(avatar);
        }
    }

    public Avatar searchByUri(String uri) {
        if(this.avatarRepository.findByUri(uri).isPresent()){
            return this.avatarRepository.findByUri(uri).get();
        }else{
            return null;
        }
    }
}
