package model.util;

import model.refactor.ClassRepresentation;
import model.refactor.Refactor;
import model.refactor.Representation;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PackagesUtils {


    private static List<String> getFilesInDirectory(String relativeDirectory, String projectDirectory) {
        File projectDirectoryAsFile = new File(projectDirectory + relativeDirectory);
        String[] filesArray = projectDirectoryAsFile.list((current, name) -> new File(current, name).isFile());
        if(filesArray == null)
            return new ArrayList<>();
        List<String> files = new ArrayList<>(Arrays.asList(filesArray));
        files = files.stream().filter(file -> file.endsWith("."+Refactor.FILE_EXTENSION)).collect(Collectors.toList());
        return files;
    }

    private static List<String> getFilesInDirectoryOfFile(String relativeFilePath, String projectDirectory)
    {
        if(relativeFilePath == null)
            return new ArrayList<>();

        String directoryPath;
        if(!relativeFilePath.contains("\\")) {
            directoryPath = "";
            return getFilesInDirectory(directoryPath, projectDirectory);
        }
        else {
            directoryPath = relativeFilePath.substring(0, relativeFilePath.lastIndexOf("\\"));
            List<String> filePaths = getFilesInDirectory(directoryPath, projectDirectory);
            filePaths = filePaths.stream().map(file -> directoryPath+"\\"+file).collect(Collectors.toList());
            return filePaths;
        }
    }

    private static Representation findClassOrInterfaceByPathInFile(List<Representation> representations, List<String> PathInFile)
    {
        final Representation[] representation = new ClassRepresentation[1];
        representation[0] = null;
        representations.forEach(rep -> {
            if(rep.getOuterClassesOrInterfaces().equals(PathInFile.subList(0, PathInFile.size()-1)))
                if(rep.getName().equals(PathInFile.get(PathInFile.size()-1)))
                    representation[0] = rep;
        });
        return representation[0];
    }

    private static <T extends Representation> T findOuterClassOrInterfaceWithName(List<Representation> representations, String name, T ref)
    {
        if(representations == null)
            return null;
        List<Representation> outer = representations.stream().filter(rep -> rep.getOuterClassesOrInterfaces().isEmpty()).collect(Collectors.toList());
        final Representation[] found = new Representation[1];
        outer.forEach(out -> {
            if(out.getName().equals(name) && out.getClass().equals(ref.getClass()))
                found[0] = out;
        });
        return (T)found[0];
    }


    public static <T extends Representation> T getPublicClassOrInterfaceInFile(Map<String, List<Representation>> representationsInFiles, String filePath, T ref) {

        List<Representation> representations = representationsInFiles
                                                    .get(filePath);
        if(representations != null)
            representations = representations.stream()
                           .filter(rep -> rep.getOuterClassesOrInterfaces().isEmpty()
                           && rep.getAccessModifier().equals("public")
                           && rep.getClass().equals(ref.getClass()))
                           .collect(Collectors.toList());
        else
            return null;
        if(representations.size()==0)
            return null;
        return (T)representations.get(0);
    }

    public static <T extends Representation> T checkVisibilityInTheSameFile(Map<String,List<Representation>> representationsInFiles, T ref, String base, List<String> outerClassesOrInterfaces, String filePath) {
        List<Representation> representations = representationsInFiles.get(filePath);
        if(!outerClassesOrInterfaces.isEmpty()) {
            Representation potentialRep;
            if ((potentialRep = findBaseInOuterClasses(representations, ref, base, outerClassesOrInterfaces))!=null)
                return (T)potentialRep;
        }

        return findOuterClassOrInterfaceWithName(representations, base, ref);
    }

    public static  <T extends Representation> T checkVisibilityInTheSamePackage(Map<String,List<Representation>> representationsInFiles, T ref, String base, String filePath) {
        List<String> filesToCheck = getFilesInDirectoryOfFile(filePath, Refactor.PROJECT_DIRECTORY);
        filesToCheck.remove(filePath);
        for (String file : filesToCheck) {
            T found = getOuterClassOrInterfaceInFile(representationsInFiles, file, ref, base);
            if(found!=null && found.getClass().equals(ref.getClass()))
                    return found;
        }
        return null;
    }

    private static <T extends Representation> T getOuterClassOrInterfaceInFile(Map<String,List<Representation>> representationsInFiles, String filePath, T ref, String baseName) {
        List<Representation> representations = representationsInFiles
                .get(filePath);
        if(representations != null)
            representations = representations.stream()
                    .filter(rep -> rep.getOuterClassesOrInterfaces().isEmpty()
                            && rep.getClass().equals(ref.getClass())
                            && rep.getName().equals(baseName))
                    .collect(Collectors.toList());
        else
            return null;
        if(representations.size()==0)
            return null;
        return (T)representations.get(0);
    }

    public static <T extends Representation> T checkVisibilityByImports(Map<String,List<Representation>> representationsInFiles, List<String> imports, T ref, String base) {

        List<String> limitedImports = imports.stream().filter(imp -> imp.split("\\.")[imp.split("\\.").length-1].equals(base)||imp.endsWith("*"))
                                                        .map(imp -> imp.replace(".", "\\"))
                                                        .collect(Collectors.toList());

        for (String limitedImport : limitedImports)
        {
            if (limitedImport.endsWith("*"))
            {
                String directoryToCheck = limitedImport.substring(0, limitedImport.lastIndexOf("\\"));
                for (String file : getFilesInDirectory(directoryToCheck, Refactor.PROJECT_DIRECTORY).stream().map(f->directoryToCheck+"\\"+f).collect(Collectors.toList())) {
                    T foundPublicRep = getPublicClassOrInterfaceInFile(representationsInFiles, file, ref);
                    if (foundPublicRep != null && foundPublicRep.getName().equals(base))
                        return foundPublicRep;
                }
            }
            else
            {
                String fileToCheck = limitedImport.concat("." + Refactor.FILE_EXTENSION);
                T foundPublicRep = getPublicClassOrInterfaceInFile(representationsInFiles, fileToCheck, ref);
                if(foundPublicRep != null && foundPublicRep.getName().equals(base))
                    return foundPublicRep;
            }
        }
        return null;
    }

    private static <T extends Representation> T findBaseInOuterClasses(List<Representation> representations, T ref, String base, List<String> outerClassesOrInterfaces) {
        Representation potentialRepresentation;
        for(int i = outerClassesOrInterfaces.size()-1; i>=0; i--)
        {
            if(outerClassesOrInterfaces.get(i).equals(base)) {
                potentialRepresentation = findClassOrInterfaceByPathInFile(representations, outerClassesOrInterfaces.subList(0, i+1));
                if(potentialRepresentation != null && potentialRepresentation.getClass().equals(ref.getClass()))
                    return (T)potentialRepresentation;
            }
        }
        return null;
    }
}
