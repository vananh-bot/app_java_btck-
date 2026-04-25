package DAO;

import Model.InviteLink;
import java.util.List;

    public interface InviteLinkDAOInterface {
        void create(InviteLink link);

        InviteLink findByToken(String token);

        List<InviteLink> findByProjectId(int projectId);

        void updateActive(int id, boolean isActive);

        void delete(int id);
    }
