package com.ensitech.smart_city_iot.service;

import com.ensitech.smart_city_iot.dto.commentaireDTO.CreateCommentaireDTO;
import com.ensitech.smart_city_iot.dto.commentaireDTO.ResponseCommentaireDTO;
import com.ensitech.smart_city_iot.dto.commentaireDTO.UpdateCommentaireDTO;
import com.ensitech.smart_city_iot.entity.Citoyen;
import com.ensitech.smart_city_iot.entity.Commentaire;
import com.ensitech.smart_city_iot.entity.Utilisateur;
import com.ensitech.smart_city_iot.exception.BusinessException;
import com.ensitech.smart_city_iot.exception.EntityNotFoundException;
import com.ensitech.smart_city_iot.repository.CommentaireRepository;
import com.ensitech.smart_city_iot.repository.UtilisateurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional

@Slf4j
public class CommentaireServiceImpl implements CommentaireService{

    @Autowired
    private CommentaireRepository commentaireRepository;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Override
    public ResponseCommentaireDTO createCommentaire(CreateCommentaireDTO dto) throws Exception {
        log.info("Création d'un nouveau commentaire par le citoyen ID: {}", dto.getIdCitoyen());

        // Vérification que l'utilisateur existe et est bien un citoyen
        Utilisateur utilisateur = utilisateurRepository.findById(dto.getIdCitoyen())
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + dto.getIdCitoyen()));

        if (!(utilisateur instanceof Citoyen)) {
            throw new BusinessException("Seuls les citoyens peuvent poster des commentaires");
        }

        Citoyen citoyen = (Citoyen) utilisateur;

        // Vérification que le citoyen est actif
        if (!citoyen.isActif()) {
            throw new BusinessException("Votre compte n'est pas actif");
        }

        // Validation du contenu
        validateCommentaireContent(dto);

        // Création du commentaire
        Commentaire commentaire = buildCommentaireFromDTO(dto, citoyen);
        commentaire = commentaireRepository.save(commentaire);

        log.info("Commentaire créé avec succès: ID {}", commentaire.getIdCommentaire());
        return ResponseCommentaireDTO.fromEntity(commentaire);
    }

    @Override
    public ResponseCommentaireDTO getCommentaireById(Long id) throws Exception {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID: " + id));

        return ResponseCommentaireDTO.fromEntity(commentaire);
    }

    @Override
    public List<ResponseCommentaireDTO> getAllCommentaires() throws Exception {
        List<Commentaire> commentaires = commentaireRepository.findByActif(true);
        return commentaires.stream()
                .map(ResponseCommentaireDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ResponseCommentaireDTO> getAllCommentaires(Pageable pageable) throws Exception {
        Page<Commentaire> commentaires = commentaireRepository.findByActif(true, pageable);
        return commentaires.map(ResponseCommentaireDTO::fromEntity);
    }

    @Override
    public List<ResponseCommentaireDTO> getCommentairesByCitoyen(Long idCitoyen) throws Exception {
        Utilisateur utilisateur = utilisateurRepository.findById(idCitoyen)
                .orElseThrow(() -> new EntityNotFoundException("Citoyen non trouvé avec l'ID: " + idCitoyen));

        if (!(utilisateur instanceof Citoyen)) {
            throw new BusinessException("L'utilisateur spécifié n'est pas un citoyen");
        }

        List<Commentaire> commentaires = commentaireRepository.findByCitoyenAndActif((Citoyen) utilisateur, true);
        return commentaires.stream()
                .map(ResponseCommentaireDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseCommentaireDTO> getCommentairesBySujet(String sujet) throws Exception {
        List<Commentaire> commentaires = commentaireRepository.findBySujetAndActif(sujet, true);
        return commentaires.stream()
                .map(ResponseCommentaireDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public ResponseCommentaireDTO updateCommentaire(Long id, UpdateCommentaireDTO dto) throws Exception {
        log.info("Mise à jour du commentaire ID: {}", id);

        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID: " + id));

        if (!commentaire.isActif()) {
            throw new BusinessException("Ce commentaire ne peut plus être modifié");
        }

        // Mise à jour des champs
        updateCommentaireFields(commentaire, dto);
        commentaire = commentaireRepository.save(commentaire);

        log.info("Commentaire mis à jour avec succès: ID {}", id);
        return ResponseCommentaireDTO.fromEntity(commentaire);
    }

    @Override
    public void deleteCommentaire(Long id) throws Exception {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID: " + id));

        commentaireRepository.delete(commentaire);
        log.info("Commentaire supprimé définitivement: ID {}", id);
    }

    @Override
    public void softDeleteCommentaire(Long id) throws Exception {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID: " + id));

        commentaireRepository.softDelete(id);
        log.info("Commentaire désactivé: ID {}", id);
    }

    @Override
    public void ajouterLike(Long id) throws Exception {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID: " + id));

        if (!commentaire.isActif()) {
            throw new BusinessException("Ce commentaire n'est plus actif");
        }

        commentaireRepository.incrementLikes(id);
        log.debug("Like ajouté au commentaire ID: {}", id);
    }

    @Override
    public void retirerLike(Long id) throws Exception {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID: " + id));

        commentaireRepository.decrementLikes(id);
        log.debug("Like retiré du commentaire ID: {}", id);
    }

    @Override
    public void ajouterDislike(Long id) throws Exception {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID: " + id));

        if (!commentaire.isActif()) {
            throw new BusinessException("Ce commentaire n'est plus actif");
        }

        commentaireRepository.incrementDislikes(id);
        log.debug("Dislike ajouté au commentaire ID: {}", id);
    }

    @Override
    public void retirerDislike(Long id) throws Exception {
        Commentaire commentaire = commentaireRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Commentaire non trouvé avec l'ID: " + id));

        commentaireRepository.decrementDislikes(id);
        log.debug("Dislike retiré du commentaire ID: {}", id);
    }

    @Override
    public List<ResponseCommentaireDTO> rechercherCommentaires(String texte) throws Exception {
        List<Commentaire> commentaires = commentaireRepository.findByTitreOrContenuContaining(texte);
        return commentaires.stream()
                .map(ResponseCommentaireDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseCommentaireDTO> getCommentairesPopulaires(int limite) throws Exception {
        Pageable pageable = PageRequest.of(0, limite);
        List<Commentaire> commentaires = commentaireRepository.findMostPopular(pageable);
        return commentaires.stream()
                .map(ResponseCommentaireDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<ResponseCommentaireDTO> getCommentairesRecents(int jours) throws Exception {
        LocalDateTime dateDebut = LocalDateTime.now().minusDays(jours);
        List<Commentaire> commentaires = commentaireRepository.findRecentCommentaires(dateDebut);
        return commentaires.stream()
                .map(ResponseCommentaireDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public Long getNombreCommentairesByCitoyen(Long idCitoyen) throws Exception {
        return commentaireRepository.countByCitoyen(idCitoyen);
    }

    @Override
    public Double getNoteAverage() throws Exception {
        return commentaireRepository.getAverageNote();
    }

    // Méthodes privées
    private void validateCommentaireContent(CreateCommentaireDTO dto) {
        if (dto.getContenu().trim().length() < 10) {
            throw new BusinessException("Le contenu doit contenir au moins 10 caractères");
        }

        // Validation anti-spam basique
        if (dto.getContenu().toLowerCase().contains("spam") ||
                dto.getContenu().toLowerCase().contains("pub")) {
            throw new BusinessException("Contenu non autorisé détecté");
        }
    }

    private Commentaire buildCommentaireFromDTO(CreateCommentaireDTO dto, Citoyen citoyen) {
        return Commentaire.builder()
                .titre(dto.getTitre())
                .contenu(dto.getContenu().trim())
                .noteEvaluation(dto.getNoteEvaluation())
                .sujet(dto.getSujet())
                .localisation(dto.getLocalisation())
                .citoyen(citoyen)
                .actif(true)
                .nombreLikes(0)
                .nombreDislikes(0)
                .build();
    }

    private void updateCommentaireFields(Commentaire commentaire, UpdateCommentaireDTO dto) {
        if (dto.getTitre() != null) {
            commentaire.setTitre(dto.getTitre());
        }
        if (dto.getContenu() != null) {
            commentaire.setContenu(dto.getContenu().trim());
        }
        if (dto.getNoteEvaluation() != null) {
            commentaire.setNoteEvaluation(dto.getNoteEvaluation());
        }
        if (dto.getSujet() != null) {
            commentaire.setSujet(dto.getSujet());
        }
        if (dto.getLocalisation() != null) {
            commentaire.setLocalisation(dto.getLocalisation());
        }
        if (dto.getActif() != null) {
            commentaire.setActif(dto.getActif());
        }
    }
}
