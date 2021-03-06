package highlands.worldgen.layer;

import com.google.common.collect.ObjectArrays;

import fabricator77.multiworld.api.biomeregistry.AdvancedBiomeRegistry;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenJungle;
import net.minecraft.world.gen.layer.GenLayer;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.BiomeManager.BiomeEntry;

public class GenLayerBiomeEdgeHL extends GenLayer
{
    public GenLayerBiomeEdgeHL(long p_i45475_1_, GenLayer parent)
    {
        super(p_i45475_1_);
        this.parent = parent;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int par1, int par2, int par3, int par4)
    {
        int[] aint = this.parent.getInts(par1 - 1, par2 - 1, par3 + 2, par4 + 2);
        int[] aint1 = IntCache.getIntCache(par3 * par4);

        for (int i1 = 0; i1 < par4; ++i1)
        {
            for (int j1 = 0; j1 < par3; ++j1)
            {
                this.initChunkSeed((long)(j1 + par1), (long)(i1 + par2));
                int k1 = aint[j1 + 1 + (i1 + 1) * (par3 + 2)];
                
                if (AdvancedBiomeRegistry.biomeEntries[k1].edgeBiome >= 0) {
                	
                	//TODO: edge check
                	if (isEdge(aint, j1, i1, par3)) {
                		aint1[j1 + i1 * par3] = AdvancedBiomeRegistry.biomeEntries[k1].edgeBiome;
                	}
                	else {
                		aint1[j1 + i1 * par3] = k1;
                	}
                }
                else {
            		aint1[j1 + i1 * par3] = k1;
            	}
            }
        }

        return aint1;
    }
    
    private boolean isEdge(int[] aint, int j1, int i1, int par3) {
    	
    	// center parent biome
    	int c1 = aint[j1 + 1 + (i1 + 1) * (par3 + 2)];
    	if (AdvancedBiomeRegistry.biomeEntries[c1].type == "edge") {
    		return false;
    	}
    	// center parent edgeBiome
    	// int temp = AdvancedBiomeRegistry.biomeEntries[c1].edgeBiome;
    	// center parent hillsBiome
    	BiomeEntry[] hillsBiomes = AdvancedBiomeRegistry.biomeEntries[c1].hillsBiomes;
    	BiomeEntry[] subBiomes = AdvancedBiomeRegistry.biomeEntries[c1].subBiomes;
    	int[] likeBiomes = new int[2 + hillsBiomes.length + subBiomes.length];
    	likeBiomes[0] = c1;
    	likeBiomes[1] = AdvancedBiomeRegistry.biomeEntries[c1].edgeBiome;
    	for (int i=0; i<hillsBiomes.length; i++) {
    		likeBiomes[i+2] = hillsBiomes[i].biome.biomeID;
    	}
    	for (int i=0; i<subBiomes.length; i++) {
    		likeBiomes[i+2+hillsBiomes.length] = subBiomes[i].biome.biomeID;
    	}
    			
    	// adjacent biomes
    	int a1 = aint[j1 + 1 + (i1 + 1 - 1) * (par3 + 2)];
        int a2 = aint[j1 + 1 + 1 + (i1 + 1) * (par3 + 2)];
        int a3 = aint[j1 + 1 - 1 + (i1 + 1) * (par3 + 2)];
        int a4 = aint[j1 + 1 + (i1 + 1 + 1) * (par3 + 2)];
        // count valid matches with center
        
        int count = 0;
        //TODO: ensure mismatch isn't due to sub-biomes/hills biomes of parent
        if (likeBiome(a1, likeBiomes))
        {
            ++count;
        }

        if (likeBiome(a2, likeBiomes))
        {
            ++count;
        }

        if (likeBiome(a3, likeBiomes))
        {
            ++count;
        }

        if (likeBiome(a3, likeBiomes))
        {
            ++count;
        }

        if (count < 4)
        {
        	return true;
        }
        
    	return false;
    }
    
    private boolean likeBiome (int center, int[] likeBiomes) {
    	// matches parent biome + any sub/hills biomes
    	// also return false if any type of edge biome
    	for (int i=0; i<likeBiomes.length; i++) {
    		if (center == likeBiomes[i]) {
    			return true;
    		}
    	}
    	return false;
    }
}
