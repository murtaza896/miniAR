//package com.example.simplerestapis.service;
//
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.example.simplerestapis.models.Git;
//import com.example.simplerestapis.repository.GitRepository;
//
//@Service
//public class GitService implements IGitService {
//
//	@Autowired
//	private GitRepository gitRepository;
//
//	@Override
//	public List<Git> getGitsByUser(String user) {
//		return gitRepository.findByUsername(user);
//	}
//
//	@Override
//	public Optional<Git> getGitById(long id) {
//		return gitRepository.findById(id);
//	}
//
//	@Override
//	public void updateGit(Git git) {
//		gitRepository.save(git);
//	}
//
//
//	@Override
//	public void addGit( String username, String password, String repoURL, String message,  boolean isDone) {
//		gitRepository.save(new Git(username, password, repoURL, message, isDone));
//	}
//
//	@Override
//	public void deleteGit(long id) {
//		Optional<Git> git = gitRepository.findById(id);
//		if (git.isPresent()) {
//			gitRepository.delete(git.get());
//		}
//	}
//
//	@Override
//	public void saveGit(Git git) {
//		gitRepository.save(git);
//	}
//
//}