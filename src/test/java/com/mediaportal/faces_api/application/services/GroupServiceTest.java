package com.mediaportal.faces_api.application.services;


import com.google.gson.Gson;
import org.springframework.web.client.RestTemplate;

import java.io.File;

class GroupServiceTest {
    public static void main (String[] args){
//        RestTemplate restTemplate = new RestTemplate();
//        Gson gson = new Gson();
//
//        GroupService groupService = new GroupService(restTemplate, gson);
//        groupService.setBrahmaUrl("http://localhost:8094/");
//        groupService.readGroupJSON();

        File folder = new File("C:\\Users\\pablo\\Desktop\\script IA\\WORK_FOLDER\\Agrupamento");

        if (folder.isDirectory() && folder.exists()) {
            boolean success = deleteFolder(folder);
            if (success) {
                System.out.println("Pasta deletada com sucesso!");
            } else {
                System.out.println("Falha ao deletar a pasta.");
            }
        } else {
            System.out.println("A pasta não existe ou não é uma pasta válida.");
        }


    }

    public static boolean deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Se for um arquivo, deleta
                    if (file.isFile()) {
                        if (!file.delete()) {
                            return false;
                        }
                    }
                    // Se for uma pasta, chama recursivamente para deletar
                    else if (file.isDirectory()) {
                        if (!deleteFolder(file)) {
                            return false;
                        }
                    }
                }
            }
        }

        // Deleta a própria pasta após deletar todo o seu conteúdo
        return folder.delete();
    }
  
}