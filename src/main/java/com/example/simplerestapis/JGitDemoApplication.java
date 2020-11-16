package com.example.simplerestapis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class JGitDemoApplication {
	
	private String repoUrl;
	private String username;
	private String password;
	private String path = "D:\\DemoPresentation\\Testing";
	private String message;
	
	public JGitDemoApplication(String repoUrl,String username, String password, String message){
		this.repoUrl = repoUrl;
		this.username = username;
		this.password = password;
		this.message = message;
	}
	
	public void repoClone() throws IOException {
		try {
			File dir = new File(path);
			if(dir.exists()) {
				deleteFolder(dir);
			}
			Git git = Git.cloneRepository()
					.setURI(repoUrl)
					.setCredentialsProvider(getCredentialsProvider())
					.setDirectory(new File(path))
					.call();
			git.close();
			System.out.println("Checkout Successful");
		} catch (GitAPIException e) {
			System.err.println(e.getMessage());
		}
	}

	public static void copyDirectory(File sourceDir, File targetDir) throws IOException {
		if (sourceDir.isDirectory()) {
			copyDirectoryRecursively(sourceDir, targetDir);
		} else {
			Files.copy(sourceDir.toPath(), targetDir.toPath(),StandardCopyOption.REPLACE_EXISTING);
		}
	}

	private static void copyDirectoryRecursively(File source, File target) throws IOException {
		if (!target.exists()) {
			target.mkdir();
		}

		for (String child : source.list()) {
			copyDirectory(new File(source, child), new File(target, child));
		}
	}
	
	
	public void deleteFolder(File file) {
		for (File subFile : file.listFiles()) {
			if (subFile.isDirectory()) {
				deleteFolder(subFile);
			} else {
				subFile.delete();
			}
		}
		file.delete();
	}
	
	public void makeAChange() {
		FileWriter fw;
		try {
			fw = new FileWriter(new File("D:\\DemoPresentation\\Testing\\test1.txt"));
			BufferedWriter bw = new BufferedWriter(fw);
			bw.append("Updated File at " +  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date()));
			bw.flush();
			bw.close();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public CredentialsProvider getCredentialsProvider() {
		return new UsernamePasswordCredentialsProvider(username, password);
	}
	
	public void gitCommit() {
		try {
			Git git = Git.open(new File(path));
			git.add().addFilepattern(".").call();
			git.commit().setAuthor("Rohit", username).setMessage(message).call();
			git.close();
		} catch (IOException | GitAPIException e) {
			System.err.println(e.getMessage());
		}
	}
	
	public void gitPush() {
		try {
			Git git = Git.open(new File(path));
			git.push().setCredentialsProvider(getCredentialsProvider()).call();
			git.close();
		} catch (IOException | GitAPIException e) {
			System.err.println(e.getMessage());
		}
	}
}
